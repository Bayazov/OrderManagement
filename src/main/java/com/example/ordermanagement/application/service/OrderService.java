package com.example.ordermanagement.application.service;

import com.example.ordermanagement.domain.event.OrderStatusChangedEvent;
import com.example.ordermanagement.domain.exception.InvalidOrderException;
import com.example.ordermanagement.domain.exception.OrderNotFoundException;
import com.example.ordermanagement.domain.exception.TotalPriceMismatchException;
import com.example.ordermanagement.domain.model.Order;
import com.example.ordermanagement.domain.model.User;
import com.example.ordermanagement.domain.repository.OrderRepository;
import com.example.ordermanagement.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    // Создаем заказ
    @Transactional
    public Order createOrder(String username, Order order) {
        logger.info("Creating order for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidOrderException("User not found"));
        order.setUser(user);
        validateOrder(order);
        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully with ID: {}", savedOrder.getOrderId());
        return savedOrder;
    }

    // Обновляем заказ
    @Transactional
    public Order updateOrder(String username, Long orderId, Order updatedOrder) {
        logger.info("Updating order with ID: {} for user: {}", orderId, username);
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidOrderException("User not found"));

        if (!existingOrder.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("You don't have permission to update this order");
        }

        Order.OrderStatus oldStatus = existingOrder.getStatus();

        existingOrder.setCustomerName(updatedOrder.getCustomerName());
        existingOrder.setStatus(updatedOrder.getStatus());
        existingOrder.setTotalPrice(updatedOrder.getTotalPrice());
        existingOrder.updateProducts(updatedOrder.getProducts());

        validateOrder(existingOrder);

        Order savedOrder = orderRepository.save(existingOrder);
        logger.info("Order updated successfully with ID: {}", savedOrder.getOrderId());

        if (oldStatus != savedOrder.getStatus()) {
            OrderStatusChangedEvent event = new OrderStatusChangedEvent(savedOrder.getOrderId(), oldStatus, savedOrder.getStatus());
            eventPublisher.publishEvent(event);
            logger.info("Published order status changed event for order ID: {}", savedOrder.getOrderId());
        }

        return savedOrder;
    }

    // Получаем список заказов
    public List<Order> getOrders(String username, Order.OrderStatus status, BigDecimal minPrice, BigDecimal maxPrice) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidOrderException("User not found"));
        if (user.getRole() == User.Role.ADMIN) {
            return orderRepository.findByStatusAndPriceRange(status, minPrice, maxPrice);
        } else {
            return orderRepository.findByUserAndStatusAndPriceRange(user, status, minPrice, maxPrice);
        }
    }

    // Получаем конкретный заказ
    public Order getOrder(String username, Long orderId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidOrderException("User not found"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (user.getRole() == User.Role.ADMIN || order.getUser().getId().equals(user.getId())) {
            return order;
        } else {
            throw new AccessDeniedException("You don't have permission to access this order");
        }
    }

    // Удаляем заказ
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        orderRepository.delete(order);
        logger.info("Order deleted successfully with ID: {}", orderId);
    }

    // Проверяем валидность заказа
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

        if (order.getTotalPrice() == null || !order.getTotalPrice().setScale(2, RoundingMode.HALF_UP).equals(calculatedTotal)) {
            throw new TotalPriceMismatchException("Total price does not match the sum of product prices");
        }
    }
}

