package com.example.ordermanagement.domain.event;

import com.example.ordermanagement.domain.model.Order;
import lombok.Data;

@Data
public class OrderStatusChangedEvent {
    private final Long orderId;
    private final Order.OrderStatus oldStatus;
    private final Order.OrderStatus newStatus;
}