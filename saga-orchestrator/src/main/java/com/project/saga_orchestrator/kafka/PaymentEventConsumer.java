package com.project.saga_orchestrator.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventConsumer {

    @KafkaListener(topics = "payment-success", groupId = "saga-group")
    public void handleSuccess(String message) {
        System.out.println("Payment SUCCESS for order: " + message);
        // Next step (later): inventory service
        // For now:
        System.out.println("Order completed successfully");
    }

    @KafkaListener(topics = "payment-failed", groupId = "saga-group")
    public void handleFailure(String message) {
        System.out.println("Payment FAILED for order: " + message);
        // Compensation logic
        System.out.println("Rolling back order (cancelled)");
    }
}