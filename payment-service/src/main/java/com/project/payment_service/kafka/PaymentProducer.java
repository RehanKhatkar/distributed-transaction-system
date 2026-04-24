package com.project.payment_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.payment_service.model.OrderEvent;
import com.project.payment_service.model.OrderStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    public PaymentProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate; this.objectMapper = objectMapper;
    }
    public void sendPaymentSuccess(String orderId,String correlationId) {
        try {
            OrderEvent event = new OrderEvent(orderId, OrderStatus.PAYMENT_SUCCESS,correlationId);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("payment-success", orderId, json); System.out.println("Sent: " + json);
        }
        catch (Exception e) {
            System.out.println("Failed to send payment-success event for order: " + orderId);
        }
    }
    public void sendPaymentFailed(String orderId,String correlationId) {
        try {
            OrderEvent event = new OrderEvent(orderId, OrderStatus.PAYMENT_FAILED,correlationId);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("payment-failed", orderId, json);
        }
        catch (Exception e) {
            System.out.println("Failed to send payment-failed event for order: " + orderId);
        }
    }
    public void sendRefundSuccess(String orderId,String correlationId) {
        try {
            OrderEvent event = new OrderEvent(orderId, OrderStatus.REFUND_SUCCESS,correlationId);
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("refund-success", orderId, json);
        }
        catch (Exception e) {
            System.out.println("Failed to send refund-success event for order: " + orderId);
        }
    }
}