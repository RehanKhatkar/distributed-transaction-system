package com.project.orderservice.kafka;
import com.project.orderservice.model.OrderEvent;
import com.project.orderservice.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;

@Service
public class OrderProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);
    public OrderProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    public void sendOrderCreated(String orderId) {
        String correlationId = UUID.randomUUID().toString();
        try {
            OrderEvent event = new OrderEvent(orderId, OrderStatus.CREATED,correlationId);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("order-created", orderId, json);
            log.info("[{}] Sent order-created event: {}", correlationId, json);
        } catch (Exception e) {
            log.error("[{}] Failed to send order-created event for orderId: {}", correlationId, orderId, e);
        }
    }
}