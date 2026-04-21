package com.project.inventory_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryConsumer {

    private final InventoryProducer inventoryProducer;

    public InventoryConsumer(InventoryProducer inventoryProducer) {
        this.inventoryProducer = inventoryProducer;
    }

    @KafkaListener(topics = "inventory-request", groupId = "inventory-group")
    public void consume(String message) {

        System.out.println("Received inventory request: " + message);

        // simulate stock availability
        boolean inStock = Math.random() > 0.3;

        if (inStock) {
            inventoryProducer.sendEvent("inventory-success", message);
        } else {
            inventoryProducer.sendEvent("inventory-failed", message);
        }
    }
}