package com.project.payment_service.model;

public enum OrderStatus {
    CREATED,
    PENDING,
    PAYMENT_REQUEST,
    PAYMENT_SUCCESS,
    PAYMENT_FAILED,
    INVENTORY_REQUEST,
    INVENTORY_SUCCESS,
    INVENTORY_FAILED,
    REFUND_REQUEST,
    REFUND_SUCCESS,
    COMPLETED,
    REFUNDED
}