package com.project.payment_service.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {

    private final PaymentProducer paymentProducer;

    public PaymentConsumer(PaymentProducer paymentProducer) {
        this.paymentProducer = paymentProducer;
    }

    @KafkaListener(topics = "payment-request", groupId = "payment-group")
    public void consume(String message) {

        System.out.println("Received payment request: " + message);

        // simulate payment processing
        boolean success = Math.random() > 0.2;

        if (success) {
            paymentProducer.sendEvent("payment-success", message);
        } else {
            paymentProducer.sendEvent("payment-failed", message);
        }
    }
}