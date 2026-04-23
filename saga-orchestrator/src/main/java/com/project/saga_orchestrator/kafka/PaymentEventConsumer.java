package com.project.saga_orchestrator.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.saga_orchestrator.model.OrderEvent;
import com.project.saga_orchestrator.model.SagaState;
import com.project.saga_orchestrator.repo.SagaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Service
public class PaymentEventConsumer {
    private final SagaRepository sagaRepository;
    private final SagaProducer sagaProducer;
    private final ObjectMapper objectMapper;
    public PaymentEventConsumer(SagaRepository sagaRepository, SagaProducer sagaProducer, ObjectMapper objectMapper) {
        this.sagaRepository = sagaRepository;
        this.sagaProducer = sagaProducer;
        this.objectMapper = objectMapper;
    }
    @KafkaListener(topics = "payment-success", groupId = "saga-group")
    public void handleSuccess(String message) {
        String orderId;
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            orderId = event.getOrderId();
        } catch (Exception e) {
            orderId = message;
        }

        try {
            SagaState state = sagaRepository.findById(orderId).orElse(null);
            if (state == null) {
                throw new RuntimeException("Order not found");
            }
            if ("PAYMENT_SUCCESS".equals(state.getStatus())) {
                System.out.println("Duplicate ignored");
                return;
            }
            state.setStatus("PAYMENT_SUCCESS");
            sagaRepository.save(state);
            sagaProducer.sendEventJson("inventory-failed", orderId, "INVENTORY_FAILED");
            System.out.println("Payment success processed for: " + orderId);
        } catch (Exception e) {
            sagaProducer.sendEventJson("payment-success-dlq", orderId, "PAYMENT_SUCCESS_FAILED");
        }
    }
    @KafkaListener(topics = "payment-failed", groupId = "saga-group")
    public void handleFailure(String message) {
        String orderId;
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            orderId = event.getOrderId();
        } catch (Exception e) {
            orderId = message;
        }
        try {
            SagaState state = sagaRepository.findById(orderId).orElse(null);
            if (state == null) {
                throw new RuntimeException("Order not found");
            }
            if ("PAYMENT_FAILED".equals(state.getStatus())) {
                System.out.println("Duplicate ignored");
                return;
            }
            state.setStatus("PAYMENT_FAILED");
            sagaRepository.save(state);
            System.out.println("Order cancelled: " + orderId);
        } catch (Exception e) {
            sagaProducer.sendEventJson("payment-failed-dlq", orderId, "PAYMENT_FAILED_FAILED");
        }
    }
}