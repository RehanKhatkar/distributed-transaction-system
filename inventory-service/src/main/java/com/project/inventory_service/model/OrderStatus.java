package com.project.inventory_service.model;

public enum OrderStatus {
    CREATED,
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