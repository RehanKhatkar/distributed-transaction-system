package com.project.payment_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.payment_service.model.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {

    private final PaymentProducer paymentProducer;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);
    public PaymentConsumer(PaymentProducer paymentProducer, ObjectMapper objectMapper) {
        this.paymentProducer = paymentProducer;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "payment-request", groupId = "payment-group")
    public void consume(String message) throws JsonProcessingException {
        OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
        String orderId = event.getOrderId();
        String correlationId= event.getCorrelationId();
        log.info("[{}] Received payment request for orderId: {}", correlationId, orderId);
        // simulate payment processing
        boolean success = Math.random() > 0.2;
        if (success) {
            paymentProducer.sendPaymentSuccess(orderId,correlationId);
        } else {
            paymentProducer.sendPaymentFailed(orderId,correlationId);
        }
    }
}