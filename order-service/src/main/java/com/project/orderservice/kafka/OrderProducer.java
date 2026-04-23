package com.project.orderservice.kafka;

import com.project.orderservice.model.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
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
            OrderEvent event = new OrderEvent(orderId, "CREATED");
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("order-created", orderId, json);
            System.out.println("Sent: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}