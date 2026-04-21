package com.project.payment_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RefundConsumer {
    private final PaymentProducer paymentProducer;
    public RefundConsumer(PaymentProducer paymentProducer) {
        this.paymentProducer = paymentProducer;
    }
    @KafkaListener(topics = "refund-request", groupId = "payment-group")
    public void consume(String message) {
        System.out.println("Processing refund for order: " + message);
        // simulate refund success
        paymentProducer.sendEvent("refund-success", message);
    }
}