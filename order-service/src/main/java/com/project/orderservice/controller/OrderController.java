package com.project.orderservice.controller;

import com.project.orderservice.DTO.OrderRequest;
import com.project.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }
}