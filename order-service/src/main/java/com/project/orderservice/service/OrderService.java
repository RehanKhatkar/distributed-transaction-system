package com.project.orderservice.service;

import com.project.orderservice.DTO.OrderRequest;
import com.project.orderservice.entity.Order;
import com.project.orderservice.kafka.OrderProducer;
import com.project.orderservice.model.OrderEvent;
import com.project.orderservice.model.OrderStatus;
import com.project.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
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
            String correlationId = event.getCorrelationId();
            long id = Long.parseLong(event.getOrderId());
            log.info("[{}] Received order-completed event for orderId: {}", correlationId, id);
            Order order = orderRepository.findById(id).orElse(null);
            if (order != null) {
                order.setStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);
                log.info("[{}] Order COMPLETED updated in DB for orderId: {}", correlationId, id);
            } else {
                log.warn("[{}] Order not found in DB for orderId: {}", correlationId, id);
            }
        } catch (Exception e) {
            log.error("Failed to process order-completed event. Message: {}", message, e);
        }
    }
    @KafkaListener(topics = "order-refunded", groupId = "order-group")
    public void handleRefunded(String message) {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            String correlationId = event.getCorrelationId();
            long id = Long.parseLong(event.getOrderId());
            log.info("[{}] Received order-refunded event for orderId: {}", correlationId, id);
            Order order = orderRepository.findById(id).orElse(null);
            if (order != null) {
                order.setStatus(OrderStatus.REFUNDED);
                orderRepository.save(order);
                log.info("[{}] Order REFUNDED updated in DB for orderId: {}", correlationId, id);
            } else {
                log.warn("[{}] Order not found in DB to refund for orderId: {}", correlationId, id);
            }
        } catch (Exception e) {
            log.error("Failed to process order-refunded event. Message: {}", message, e);
        }
    }
}