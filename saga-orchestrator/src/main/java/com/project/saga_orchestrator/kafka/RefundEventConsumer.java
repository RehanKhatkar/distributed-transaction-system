package com.project.saga_orchestrator.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.saga_orchestrator.model.OrderEvent;
import com.project.saga_orchestrator.model.OrderStatus;
import com.project.saga_orchestrator.model.SagaState;
import com.project.saga_orchestrator.repo.SagaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RefundEventConsumer {
    private final SagaRepository sagaRepository;
    private final ObjectMapper objectMapper;
    public RefundEventConsumer(SagaRepository sagaRepository, ObjectMapper objectMapper) {
        this.sagaRepository = sagaRepository;
        this.objectMapper = objectMapper;
    }
    @KafkaListener(topics = "refund-success", groupId = "saga-group")
    public void handleRefund(String message) {
        String orderId;
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            orderId = event.getOrderId();
        } catch (Exception e) {
            orderId = message;
        }
        SagaState state = sagaRepository.findById(orderId).orElse(null);
        if (state != null) {
            state.setStatus(OrderStatus.REFUNDED);
            sagaRepository.save(state);
        }
        System.out.println("Refund completed: " + orderId);
    }
}
