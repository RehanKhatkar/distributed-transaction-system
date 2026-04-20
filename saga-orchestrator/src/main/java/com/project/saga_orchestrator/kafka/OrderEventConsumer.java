package com.project.saga_orchestrator.kafka;

import com.project.saga_orchestrator.model.SagaState;
import com.project.saga_orchestrator.repo.SagaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer {

    private final SagaProducer sagaProducer;
    private final SagaRepository sagaRepository;
    public OrderEventConsumer(SagaProducer sagaProducer, SagaRepository sagaRepository) {
        this.sagaProducer = sagaProducer;
        this.sagaRepository = sagaRepository;
    }
    @KafkaListener(topics = "order-created", groupId = "saga-group")
    public void consume(String message) {

        System.out.println("Received order-created: " + message);

        SagaState state = new SagaState(message, "CREATED");
        sagaRepository.save(state);

        sagaProducer.sendEvent("payment-request", message);
    }
}