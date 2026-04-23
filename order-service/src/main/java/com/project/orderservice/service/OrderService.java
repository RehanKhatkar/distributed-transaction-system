package com.project.orderservice.service;

import com.project.orderservice.entity.Order;
import com.project.orderservice.kafka.OrderProducer;
import com.project.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    public OrderService(OrderRepository orderRepository, OrderProducer orderProducer) {
        this.orderRepository = orderRepository;
        this.orderProducer = orderProducer;
    }

    public Order createOrder(Long userId, Long productId) {
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setStatus("PENDING");

        Order savedOrder = orderRepository.save(order);

        // Send event to Kafka
        String orderId = String.valueOf(order.getId());
        orderProducer.sendOrderCreated(orderId);
        return savedOrder;
    }
}