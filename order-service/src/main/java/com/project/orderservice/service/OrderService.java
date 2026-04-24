package com.project.orderservice.service;

import com.project.orderservice.DTO.OrderRequest;
import com.project.orderservice.entity.Order;
import com.project.orderservice.kafka.OrderProducer;
import com.project.orderservice.model.OrderEvent;
import com.project.orderservice.model.OrderStatus;
import com.project.orderservice.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository, OrderProducer orderProducer, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.orderProducer = orderProducer;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<Long> createOrder(OrderRequest request) {

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setProductId(request.getProductId());
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);
        orderProducer.sendOrderCreated(String.valueOf(order.getId()));
        return ResponseEntity.ok(order.getId());
    }
    @KafkaListener(topics = "order-completed", groupId = "order-group")
    public void handleCompleted(String message) {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            long id=Long.parseLong(event.getOrderId());
            Order order = orderRepository.findById(id).orElse(null);
            if (order != null) {
                order.setStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);
            }
            System.out.println("Order COMPLETED updated in DB: " + event.getOrderId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @KafkaListener(topics = "order-refunded", groupId = "order-group")
    public void handleRefunded(String message) {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            long id=Long.parseLong(event.getOrderId());
            Order order = orderRepository.findById(id).orElse(null);
            if (order != null) {
                order.setStatus(OrderStatus.REFUNDED);
                orderRepository.save(order);
            }
            System.out.println("Order REFUNDED updated in DB: " + event.getOrderId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}