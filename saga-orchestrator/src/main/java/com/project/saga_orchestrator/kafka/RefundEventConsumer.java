package com.project.saga_orchestrator.kafka;

import com.project.saga_orchestrator.model.SagaState;
import com.project.saga_orchestrator.repo.SagaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RefundEventConsumer {
    private final SagaRepository sagaRepository;
    public RefundEventConsumer(SagaRepository sagaRepository) {
        this.sagaRepository = sagaRepository;
    }
    @KafkaListener(topics = "refund-success", groupId = "saga-group")
    public void handleRefund(String message) {
        SagaState state = sagaRepository.findById(message).orElse(null);
        if (state != null) {
            state.setStatus("REFUNDED");
            sagaRepository.save(state);
        }
        System.out.println("Refund completed for order: " + message);
    }
}
