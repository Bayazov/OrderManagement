package com.example.ordermanagement.application.service;

import com.example.ordermanagement.domain.model.Order;
import com.example.ordermanagement.domain.repository.OrderRepository;
import com.example.ordermanagement.domain.event.OrderStatusChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepository, eventPublisher);
    }

    @Test
    void createOrder_ShouldSaveAndReturnOrder() {
        Order order = new Order();
        order.setCustomerName("John Doe");
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalPrice(BigDecimal.valueOf(100));

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order createdOrder = orderService.createOrder(order);

        assertNotNull(createdOrder);
        assertEquals("John Doe", createdOrder.getCustomerName());
        assertEquals(Order.OrderStatus.PENDING, createdOrder.getStatus());
        assertEquals(BigDecimal.valueOf(100), createdOrder.getTotalPrice());

        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void updateOrder_ShouldUpdateAndReturnOrder() {
        Long orderId = 1L;
        Order existingOrder = new Order();
        existingOrder.setOrderId(orderId);
        existingOrder.setCustomerName("John Doe");
        existingOrder.setStatus(Order.OrderStatus.PENDING);
        existingOrder.setTotalPrice(BigDecimal.valueOf(100));

        Order updatedOrder = new Order();
        updatedOrder.setOrderId(orderId);
        updatedOrder.setCustomerName("Jane Doe");
        updatedOrder.setStatus(Order.OrderStatus.CONFIRMED);
        updatedOrder.setTotalPrice(BigDecimal.valueOf(150));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        Order result = orderService.updateOrder(orderId, updatedOrder);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getCustomerName());
        assertEquals(Order.OrderStatus.CONFIRMED, result.getStatus());
        assertEquals(BigDecimal.valueOf(150), result.getTotalPrice());

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(eventPublisher, times(1)).publishEvent(any(OrderStatusChangedEvent.class));
    }

    @Test
    void getOrders_ShouldReturnFilteredOrders() {
        Order.OrderStatus status = Order.OrderStatus.PENDING;
        BigDecimal minPrice = BigDecimal.valueOf(50);
        BigDecimal maxPrice = BigDecimal.valueOf(150);

        List<Order> orders = Arrays.asList(
                new Order(1L, "John Doe", Order.OrderStatus.PENDING, BigDecimal.valueOf(100), null),
                new Order(2L, "Jane Doe", Order.OrderStatus.CONFIRMED, BigDecimal.valueOf(200), null)
        );

        when(orderRepository.findByStatusAndPriceRange(status, minPrice, maxPrice)).thenReturn(orders);

        List<Order> result = orderService.getOrders(status, minPrice, maxPrice);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getCustomerName());
        assertEquals("Jane Doe", result.get(1).getCustomerName());

        verify(orderRepository, times(1)).findByStatusAndPriceRange(status, minPrice, maxPrice);
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        Long orderId = 1L;
        Order order = new Order(orderId, "John Doe", Order.OrderStatus.PENDING, BigDecimal.valueOf(100), null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals("John Doe", result.getCustomerName());
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());
        assertEquals(BigDecimal.valueOf(100), result.getTotalPrice());

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void deleteOrder_ShouldCancelOrder() {
        Long orderId = 1L;
        Order order = new Order(orderId, "John Doe", Order.OrderStatus.PENDING, BigDecimal.valueOf(100), null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.deleteOrder(orderId);

        assertEquals(Order.OrderStatus.CANCELLED, order.getStatus());

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }
}

