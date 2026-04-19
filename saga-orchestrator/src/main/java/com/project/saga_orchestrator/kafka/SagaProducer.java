package com.project.saga_orchestrator.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SagaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public SagaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(String topic, String message) {
        kafkaTemplate.send(topic, message);
        System.out.println("Sent event to " + topic + ": " + message);
    }
}