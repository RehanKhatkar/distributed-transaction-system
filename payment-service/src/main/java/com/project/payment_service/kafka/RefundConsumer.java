package com.project.payment_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.payment_service.model.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RefundConsumer {
    private final PaymentProducer paymentProducer;
    private final ObjectMapper objectMapper;
    public RefundConsumer(PaymentProducer paymentProducer, ObjectMapper objectMapper) {
        this.paymentProducer = paymentProducer;
        this.objectMapper = objectMapper;
    }
    @KafkaListener(topics = "refund-request", groupId = "payment-group")
    public void consume(String message) throws JsonProcessingException {
        OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
        String orderId = event.getOrderId();
        String correlationId = event.getCorrelationId();
        System.out.println("Processing refund for order: " + message);
        // simulate refund success
        paymentProducer.sendRefundSuccess(orderId,correlationId);
    }
}