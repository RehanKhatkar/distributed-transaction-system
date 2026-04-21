package com.project.saga_orchestrator.kafka;

import com.project.saga_orchestrator.model.SagaState;
import com.project.saga_orchestrator.repo.SagaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Service
public class PaymentEventConsumer {
    private final SagaRepository sagaRepository;
    private final SagaProducer sagaProducer;
    public PaymentEventConsumer(SagaRepository sagaRepository, SagaProducer sagaProducer) {
        this.sagaRepository = sagaRepository;
        this.sagaProducer = sagaProducer;
    }
    @KafkaListener(topics = "payment-success", groupId = "saga-group")
    public void handleSuccess(String message) {
        try {
            SagaState state = sagaRepository.findById(message).orElse(null);
            if (state == null) {
                throw new RuntimeException("Order not found");
            }
            if ("PAYMENT_SUCCESS".equals(state.getStatus())) {
                System.out.println("Duplicate ignored");
                return;
            }
            // simulate failure (for testing)
            if (Math.random() < 0.3) {
                throw new RuntimeException("Random failure");
            }
            state.setStatus("PAYMENT_SUCCESS");
            sagaRepository.save(state);
            sagaProducer.sendEvent("inventory-request", message);
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            sagaProducer.sendEvent("payment-success-dlq", message);
        }
    }
    @KafkaListener(topics = "payment-failed", groupId = "saga-group")
    public void handleFailure(String message) {
        try {
            SagaState state = sagaRepository.findById(message).orElse(null);
            if (state == null) {
                throw new RuntimeException("Order not found");
            }
            if ("PAYMENT_FAILED".equals(state.getStatus())) {
                System.out.println("Duplicate ignored");
                return;
            }
            state.setStatus("PAYMENT_FAILED");
            sagaRepository.save(state);
            System.out.println("Order cancelled");
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            sagaProducer.sendEvent("payment-failed-dlq", message);
        }
    }
}