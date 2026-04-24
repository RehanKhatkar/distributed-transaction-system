package com.project.saga_orchestrator.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.saga_orchestrator.model.OrderEvent;
import com.project.saga_orchestrator.model.OrderStatus;
import com.project.saga_orchestrator.model.SagaState;
import com.project.saga_orchestrator.repo.SagaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Service
public class PaymentEventConsumer {
    private final SagaRepository sagaRepository;
    private final SagaProducer sagaProducer;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);
    public PaymentEventConsumer(SagaRepository sagaRepository, SagaProducer sagaProducer, ObjectMapper objectMapper) {
        this.sagaRepository = sagaRepository;
        this.sagaProducer = sagaProducer;
        this.objectMapper = objectMapper;
    }
    @KafkaListener(topics = "payment-success", groupId = "saga-group")
    public void handleSuccess(String message) throws JsonProcessingException {
        OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
        String orderId = event.getOrderId();
        String correlationId= event.getCorrelationId();
        try {
            SagaState state = sagaRepository.findById(orderId).orElse(null);
            if (state == null) {
                throw new RuntimeException("Order not found");
            }
            if (OrderStatus.PAYMENT_SUCCESS.equals(state.getStatus())) {
                System.out.println("Duplicate ignored");
                return;
            }
            state.setStatus(OrderStatus.PAYMENT_SUCCESS);
            sagaRepository.save(state);
            sagaProducer.sendEventJson("inventory-request", orderId, OrderStatus.INVENTORY_REQUEST,correlationId);
            log.info("[{}] Payment success processed for orderId: {}", correlationId, orderId);
        } catch (Exception e) {
            sagaProducer.sendEventJson("payment-success-dlq", orderId,OrderStatus.PAYMENT_SUCCESS_DLQ,correlationId);
        }
    }
    @KafkaListener(topics = "payment-failed", groupId = "saga-group")
    public void handleFailure(String message) throws JsonProcessingException {
        OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
        String orderId = event.getOrderId();
        String correlationId= event.getCorrelationId();
        try {
            SagaState state = sagaRepository.findById(orderId).orElse(null);
            if (state == null) {
                throw new RuntimeException("Order not found");
            }
            if (OrderStatus.PAYMENT_FAILED.equals(state.getStatus())) {
                System.out.println("Duplicate ignored");
                return;
            }
            state.setStatus(OrderStatus.PAYMENT_FAILED);
            sagaRepository.save(state);
            log.info("[{}] Order cancelled for orderId: {}", correlationId, orderId);
        } catch (Exception e) {
            sagaProducer.sendEventJson("payment-failed-dlq", orderId, OrderStatus.PAYMENT_FAILED_DLQ,correlationId);
        }
    }
}