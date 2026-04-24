package com.project.orderservice.DTO;

import lombok.Data;

@Data
public class OrderRequest {
    private Long userId;
    private Long productId;
}