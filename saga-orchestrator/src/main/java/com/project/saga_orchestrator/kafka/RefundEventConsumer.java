package com.project.saga_orchestrator.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.saga_orchestrator.model.OrderEvent;
import com.project.saga_orchestrator.model.OrderStatus;
import com.project.saga_orchestrator.model.SagaState;
import com.project.saga_orchestrator.repo.SagaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RefundEventConsumer {
    private final SagaRepository sagaRepository;
    private final ObjectMapper objectMapper;
    private final SagaProducer sagaProducer;
    private static final Logger log = LoggerFactory.getLogger(RefundEventConsumer.class);
    public RefundEventConsumer(SagaRepository sagaRepository, ObjectMapper objectMapper, SagaProducer sagaProducer) {
        this.sagaRepository = sagaRepository;
        this.objectMapper = objectMapper;
        this.sagaProducer = sagaProducer;
    }
    @KafkaListener(topics = "refund-success", groupId = "saga-group")
    public void handleRefund(String message) throws JsonProcessingException {
        OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
        String orderId = event.getOrderId();
        String correlationId= event.getCorrelationId();
        SagaState state = sagaRepository.findById(orderId).orElse(null);
        if (state != null) {
            state.setStatus(OrderStatus.REFUNDED);
            sagaRepository.save(state);
        }
        sagaProducer.sendEventJson("order-refunded", orderId, OrderStatus.REFUNDED, correlationId);
        log.info("[{}] Order Refunded for orderId: {}", correlationId, orderId);
    }
}
