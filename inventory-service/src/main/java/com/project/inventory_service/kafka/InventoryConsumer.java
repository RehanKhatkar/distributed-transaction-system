package com.project.inventory_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.inventory_service.model.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryConsumer {

    private final InventoryProducer inventoryProducer;
    private final ObjectMapper objectMapper;
    public InventoryConsumer(InventoryProducer inventoryProducer, ObjectMapper objectMapper) {
        this.inventoryProducer = inventoryProducer;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "inventory-request", groupId = "inventory-group")
    public void consume(String message) {
        String orderId;
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            orderId = event.getOrderId();
        } catch (Exception e) {
            orderId = message;
        }
        System.out.println("Processing inventory for: " + orderId);

        boolean inStock = Math.random() > 0.3;
        if (inStock) {
            inventoryProducer.sendInventorySuccess(orderId);
        } else {
            inventoryProducer.sendInventoryFailed(orderId);
        }
    }
}