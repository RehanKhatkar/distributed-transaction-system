package com.project.payment_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.payment_service.model.OrderEvent;
import com.project.payment_service.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(PaymentProducer.class);
    public PaymentProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate; this.objectMapper = objectMapper;
    }
    public void sendPaymentSuccess(String orderId,String correlationId) {
        try {
            OrderEvent event = new OrderEvent(orderId, OrderStatus.PAYMENT_SUCCESS,correlationId);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("payment-success", orderId, json);
            log.info("[{}] Sent payment-success event for orderId: {}", correlationId, orderId);
        }
        catch (Exception e) {
            log.error("[{}] Failed to send payment-success event for orderId: {}", correlationId, orderId,e);
        }
    }
    public void sendPaymentFailed(String orderId,String correlationId) {
        try {
            OrderEvent event = new OrderEvent(orderId, OrderStatus.PAYMENT_FAILED,correlationId);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("payment-failed", orderId, json);
        }
        catch (Exception e) {
            log.error("[{}] Failed to send payment-failed event for orderId: {}", correlationId, orderId,e);
        }
    }
    public void sendRefundSuccess(String orderId,String correlationId) {
        try {
            OrderEvent event = new OrderEvent(orderId, OrderStatus.REFUND_SUCCESS,correlationId);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("refund-success", orderId, json);
        }
        catch (Exception e) {
            log.error("[{}] Failed to send refund-success event for orderId: {}", correlationId, orderId,e);
        }
    }
}