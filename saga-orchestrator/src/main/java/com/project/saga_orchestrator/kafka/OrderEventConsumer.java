package com.project.saga_orchestrator.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer {

    private final SagaProducer sagaProducer;

    public OrderEventConsumer(SagaProducer sagaProducer) {
        this.sagaProducer = sagaProducer;
    }

    @KafkaListener(topics = "order-created", groupId = "saga-group")
    public void consume(String message) {
        System.out.println("Received order-created event: " + message);
        // Step 1 of saga: trigger payment
        sagaProducer.sendEvent("payment-request", message);
    }
}