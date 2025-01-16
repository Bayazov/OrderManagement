package com.example.ordermanagement.domain;

import lombok.Data;

@Data
public class OrderStatusChangedEvent {
    private final Long orderId;
    private final Order.OrderStatus oldStatus;
    private final Order.OrderStatus newStatus;
}