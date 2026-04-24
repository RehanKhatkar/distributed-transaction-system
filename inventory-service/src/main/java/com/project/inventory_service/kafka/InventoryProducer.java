package com.project.inventory_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.inventory_service.model.OrderEvent;
import com.project.inventory_service.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class InventoryProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(InventoryProducer.class);
    public InventoryProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendInventorySuccess(String orderId,String correlationId) {
        try {
            OrderEvent event = new OrderEvent(orderId, OrderStatus.INVENTORY_SUCCESS,correlationId);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("inventory-success", orderId, json);
        } catch (Exception e) {
            log.error("[{}] Unable to send event in inventory-success for orderId: {}", correlationId, orderId,e);
        }
    }
    public void sendInventoryFailed(String orderId,String correlationId) {
        try {
            OrderEvent event = new OrderEvent(orderId, OrderStatus.INVENTORY_FAILED,correlationId);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("inventory-failed", orderId, json);
        } catch (Exception e) {
            log.error("[{}] Unable to send event in inventory-failed for orderId: {}", correlationId, orderId,e);
        }
    }
}