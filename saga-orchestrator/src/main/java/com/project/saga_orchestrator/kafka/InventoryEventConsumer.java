package com.project.saga_orchestrator.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryEventConsumer {

    @KafkaListener(topics = "inventory-success", groupId = "saga-group")
    public void handleSuccess(String message) {
        System.out.println("Inventory SUCCESS for order: " + message);
        System.out.println("Order COMPLETED ✅");
    }

    @KafkaListener(topics = "inventory-failed", groupId = "saga-group")
    public void handleFailure(String message) {
        System.out.println("Inventory FAILED for order: " + message);

        // Compensation
        System.out.println("Trigger refund (compensation)");
    }
}