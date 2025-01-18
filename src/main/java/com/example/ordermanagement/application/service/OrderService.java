package com.example.ordermanagement.application.service;

import com.example.ordermanagement.domain.exception.TotalPriceMismatchException;
import com.example.ordermanagement.domain.model.Order;
import com.example.ordermanagement.domain.model.Product;
import com.example.ordermanagement.domain.repository.OrderRepository;
import com.example.ordermanagement.domain.event.OrderStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import com.example.ordermanagement.domain.exception.OrderNotFoundException;
import com.example.ordermanagement.domain.exception.InvalidOrderException;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public OrderService(OrderRepository orderRepository, ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public Order createOrder(Order order) {
        logger.info("Creating new order for customer: {}", order.getCustomerName());
        // Валидируем заказ
        validateOrder(order);
        order.setDeleted(false); // Устанавливаем флаг удаления в false при создании
        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully with ID: {}", savedOrder.getOrderId());
        return savedOrder;
    }

    @Transactional
    @CacheEvict(value = "orders", key = "#id")
    public Order updateOrder(Long id, Order order) {
        logger.info("Updating order with ID: {}", id);
        validateOrder(order);

        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Order not found with ID: {}", id);
                    return new OrderNotFoundException(id);
                });

        Order.OrderStatus oldStatus = existingOrder.getStatus();

        existingOrder.setCustomerName(order.getCustomerName());
        existingOrder.setStatus(order.getStatus());
        existingOrder.setTotalPrice(order.getTotalPrice());

        existingOrder.updateProducts(order.getProducts());

        Order updatedOrder = orderRepository.save(existingOrder);
        logger.info("Order updated successfully with ID: {}", updatedOrder.getOrderId());

        if (oldStatus != updatedOrder.getStatus()) {
            OrderStatusChangedEvent event = new OrderStatusChangedEvent(updatedOrder.getOrderId(), oldStatus, updatedOrder.getStatus());
            eventPublisher.publishEvent(event);
            logger.info("Published order status changed event for order ID: {}", updatedOrder.getOrderId());
        }

        return updatedOrder;
    }












    @Cacheable(value = "orders")
    public List<Order> getOrders(Order.OrderStatus status, BigDecimal minPrice, BigDecimal maxPrice) {
        logger.info("Fetching orders with status: {}, minPrice: {}, maxPrice: {}", status, minPrice, maxPrice);
        // Получаем список заказов с фильтрацией
        return orderRepository.findByStatusAndPriceRange(status, minPrice, maxPrice);
    }

    @Cacheable(value = "orders", key = "#id")
    public Order getOrderById(Long id) {
        logger.info("Fetching order with ID: {}", id);
        // Получаем заказ по ID
        return orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Order not found with ID: {}", id);
                    return new OrderNotFoundException(id);
                });
    }

    @Transactional
    @CacheEvict(value = "orders", key = "#id")
    public void deleteOrder(Long id) {
        logger.info("Deleting order with ID: {}", id);
        // Находим заказ по ID
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Order not found with ID: {}", id);
                    return new OrderNotFoundException(id);
                });
        // Устанавливаем статус "CANCELLED" и помечаем как удаленный
        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setDeleted(true);
        // Сохраняем обновленный заказ
        orderRepository.save(order);
        logger.info("Order deleted successfully with ID: {}", id);
    }


    private void validateOrder(Order order) {
        if (order == null) {
            throw new InvalidOrderException("Order cannot be null");
        }
        if (order.getCustomerName() == null || order.getCustomerName().trim().isEmpty()) {
            throw new InvalidOrderException("Customer name is required");
        }
        if (order.getStatus() == null) {
            throw new InvalidOrderException("Order status is required");
        }
        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one product");
        }

        BigDecimal calculatedTotal = order.getProducts().stream()
                .map(product -> product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal orderTotalPrice = order.getTotalPrice().setScale(2, RoundingMode.HALF_UP);

        if (order.getTotalPrice() == null || !orderTotalPrice.equals(calculatedTotal)) {
            throw new TotalPriceMismatchException("Total price does not match the sum of product prices. Expected: " + calculatedTotal + ", but got: " + orderTotalPrice);
        }
    }
}






