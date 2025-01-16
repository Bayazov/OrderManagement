package com.example.ordermanagement.application.service;

import com.example.ordermanagement.domain.model.Order;
import com.example.ordermanagement.domain.repository.OrderRepository;
import com.example.ordermanagement.domain.event.OrderStatusChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public OrderService(OrderRepository orderRepository, ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @CacheEvict(value = "orders", allEntries = true)
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @CacheEvict(value = "orders", key = "#id")
    public Order updateOrder(Long id, Order order) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Order.OrderStatus oldStatus = existingOrder.getStatus();

        existingOrder.setCustomerName(order.getCustomerName());
        existingOrder.setStatus(order.getStatus());
        existingOrder.setTotalPrice(order.getTotalPrice());
        existingOrder.setProducts(order.getProducts());

        Order updatedOrder = orderRepository.save(existingOrder);

        if (oldStatus != updatedOrder.getStatus()) {
            eventPublisher.publishEvent(new OrderStatusChangedEvent(updatedOrder.getOrderId(), oldStatus, updatedOrder.getStatus()));
        }

        return updatedOrder;
    }

    @Cacheable(value = "orders")
    public List<Order> getOrders(Order.OrderStatus status, BigDecimal minPrice, BigDecimal maxPrice) {
        return orderRepository.findByStatusAndPriceRange(status, minPrice, maxPrice);
    }

    @Cacheable(value = "orders", key = "#id")
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @CacheEvict(value = "orders", key = "#id")
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}

