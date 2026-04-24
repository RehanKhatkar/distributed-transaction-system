package com.project.saga_orchestrator.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.saga_orchestrator.model.OrderEvent;
import com.project.saga_orchestrator.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SagaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(SagaProducer.class);
    public SagaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    public void sendEventJson(String topic, String orderId, OrderStatus status, String correlationId) {
        try {
            OrderEvent event = new OrderEvent(orderId, status,correlationId);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, orderId, json);
        } catch (Exception e) {
            log.error("[{}] Unable to send event to required microservice: {}", correlationId, orderId,e);
        }
    }
}