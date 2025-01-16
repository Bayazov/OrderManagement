package com.example.ordermanagement.application;

import com.example.ordermanagement.domain.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    Order createOrder(Order order);
    Order updateOrder(Long id, Order order);
    List<Order> getOrders(Order.OrderStatus status, BigDecimal minPrice, BigDecimal maxPrice);
    Order getOrderById(Long id);
    void deleteOrder(Long id);
}