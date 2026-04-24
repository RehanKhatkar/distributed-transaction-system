package com.project.orderservice.kafka;

import com.project.orderservice.model.OrderEvent;
import com.project.orderservice.model.OrderStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;

@Service
public class OrderProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OrderProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    public void sendOrderCreated(String orderId) {
        try {
            String correlationId = UUID.randomUUID().toString();
            OrderEvent event = new OrderEvent(orderId, OrderStatus.CREATED,correlationId);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("order-created", orderId, json);
            System.out.println("Sent: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}