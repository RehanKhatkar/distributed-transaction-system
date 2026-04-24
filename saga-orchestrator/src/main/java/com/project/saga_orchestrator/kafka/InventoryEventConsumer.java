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
public class InventoryEventConsumer {
    private final SagaProducer sagaProducer;
    private final SagaRepository sagaRepository;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(InventoryEventConsumer.class);
    public InventoryEventConsumer(SagaProducer sagaProducer, SagaRepository sagaRepository, ObjectMapper objectMapper) {
        this.sagaProducer = sagaProducer;
        this.sagaRepository = sagaRepository;
        this.objectMapper = objectMapper;
    }
    @KafkaListener(topics = "inventory-success", groupId = "saga-group")
    public void handleSuccess(String message) throws JsonProcessingException {
        OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
        String orderId = event.getOrderId();
        String correlationId= event.getCorrelationId();
        SagaState state = sagaRepository.findById(orderId).orElse(null);
        if (state != null) {
            state.setStatus(OrderStatus.COMPLETED);
            sagaRepository.save(state);
        }
        sagaProducer.sendEventJson("order-completed", orderId, OrderStatus.COMPLETED, correlationId);
        log.info("[{}] Order Completed for orderId: {}", correlationId, orderId);
    }
    @KafkaListener(topics = "inventory-failed", groupId = "saga-group")
    public void handleFailure(String message) throws JsonProcessingException {
        OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
        String orderId = event.getOrderId();
        String correlationId= event.getCorrelationId();
        System.out.println("Inventory failed, triggering refund: " + orderId);
        sagaProducer.sendEventJson("refund-request", orderId, OrderStatus.REFUND_REQUEST,correlationId);
        log.info("[{}] Refund request sent for orderId: {}", correlationId, orderId);
    }
}