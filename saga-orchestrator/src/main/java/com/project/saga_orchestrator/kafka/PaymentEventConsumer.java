package com.project.saga_orchestrator.kafka;

import com.project.saga_orchestrator.model.SagaState;
import com.project.saga_orchestrator.repo.SagaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Service
public class PaymentEventConsumer {
    private final SagaRepository sagaRepository;
    public PaymentEventConsumer(SagaRepository sagaRepository) {
        this.sagaRepository = sagaRepository;
    }
    @KafkaListener(topics = "payment-success", groupId = "saga-group")
    public void handleSuccess(String message) {
        SagaState state = sagaRepository.findById(message).orElse(null);
        if (state != null) {
            state.setStatus("PAYMENT_SUCCESS");
            sagaRepository.save(state);
        }
        System.out.println("Order completed");
    }
    @KafkaListener(topics = "payment-failed", groupId = "saga-group")
    public void handleFailure(String message) {
        SagaState state = sagaRepository.findById(message).orElse(null);
        if (state != null) {
            state.setStatus("PAYMENT_FAILED");
            sagaRepository.save(state);
        }
        System.out.println("Order cancelled");
    }
}