package com.project.saga_orchestrator.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.saga_orchestrator.model.OrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SagaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public SagaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    public void sendEventJson(String topic, String orderId, String status) {
        try {
            OrderEvent event = new OrderEvent(orderId, status);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, orderId, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}