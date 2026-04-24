package com.project.saga_orchestrator.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.saga_orchestrator.model.OrderEvent;
import com.project.saga_orchestrator.model.OrderStatus;
import com.project.saga_orchestrator.model.SagaState;
import com.project.saga_orchestrator.repo.SagaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class OrderEventConsumer {

    private final SagaProducer sagaProducer;
    private final SagaRepository sagaRepository;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    public OrderEventConsumer(SagaProducer sagaProducer, SagaRepository sagaRepository, ObjectMapper objectMapper) {
        this.sagaProducer = sagaProducer;
        this.sagaRepository = sagaRepository;
        this.objectMapper = objectMapper;
    }
    @KafkaListener(topics = "order-created", groupId = "saga-group")
    public void consume(String message) throws JsonProcessingException {
        OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
        String orderId = event.getOrderId();
        String correlationId= event.getCorrelationId();
        log.info("[{}] Order request received for order-ID: {}", correlationId, orderId);
        SagaState state = new SagaState(orderId, OrderStatus.CREATED);
        sagaRepository.save(state);
        sagaProducer.sendEventJson("payment-request", orderId, OrderStatus.PAYMENT_REQUEST,correlationId);
    }
}