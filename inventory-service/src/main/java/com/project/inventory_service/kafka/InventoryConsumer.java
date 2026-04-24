package com.project.inventory_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.inventory_service.model.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryConsumer {

    private final InventoryProducer inventoryProducer;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(InventoryConsumer.class);
    public InventoryConsumer(InventoryProducer inventoryProducer, ObjectMapper objectMapper) {
        this.inventoryProducer = inventoryProducer;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "inventory-request", groupId = "inventory-group")
    public void consume(String message) throws JsonProcessingException {
        OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
        String orderId = event.getOrderId();
        String correlationId= event.getCorrelationId();
        log.info("[{}] Processing inventory for orderId: {}", correlationId, orderId);
        boolean inStock = Math.random() > 0.3;
        if (inStock) {
            inventoryProducer.sendInventorySuccess(orderId,correlationId);
        } else {
            inventoryProducer.sendInventoryFailed(orderId,correlationId);
        }
    }
}