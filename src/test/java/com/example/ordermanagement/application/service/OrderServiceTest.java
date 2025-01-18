package com.example.ordermanagement.application.service;

import com.example.ordermanagement.domain.event.OrderStatusChangedEvent;
import com.example.ordermanagement.domain.exception.InvalidOrderException;
import com.example.ordermanagement.domain.exception.OrderNotFoundException;
import com.example.ordermanagement.domain.model.Order;
import com.example.ordermanagement.domain.model.Product;
import com.example.ordermanagement.domain.model.User;
import com.example.ordermanagement.domain.repository.OrderRepository;
import com.example.ordermanagement.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private User admin;
    private Order order;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setRole(User.Role.USER);

        admin = new User();
        admin.setId(2L);
        admin.setUsername("admin");
        admin.setRole(User.Role.ADMIN);

        order = new Order();
        order.setOrderId(1L);
        order.setUser(user);
        order.setCustomerName("Test Customer");
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalPrice(new BigDecimal("100.00"));

        Product product = new Product("Test Product", new BigDecimal("100.00"), 1);
        product.setOrder(order);
        order.setProducts(List.of(product));
    }

    @Test
    void createOrder_ValidOrder_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrder("user", order);

        assertNotNull(result);
        assertEquals(order.getOrderId(), result.getOrderId());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_InvalidUser_ThrowsException() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(InvalidOrderException.class, () -> orderService.createOrder("nonexistent", order));
    }

    @Test
    void updateOrder_ValidOrderAndUser_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = new Order();
        updatedOrder.setCustomerName("Updated Customer Name");
        updatedOrder.setStatus(Order.OrderStatus.CONFIRMED);
        updatedOrder.setTotalPrice(new BigDecimal("150.00"));
        Product updatedProduct = new Product("Updated Product", new BigDecimal("150.00"), 1);
        updatedOrder.setProducts(List.of(updatedProduct));

        Order result = orderService.updateOrder("user", 1L, updatedOrder);

        assertNotNull(result);
        assertEquals(Order.OrderStatus.CONFIRMED, result.getStatus());
        assertEquals(new BigDecimal("150.00"), result.getTotalPrice());
        assertEquals(1, result.getProducts().size());
        assertEquals("Updated Product", result.getProducts().get(0).getName());
        verify(eventPublisher).publishEvent(any(OrderStatusChangedEvent.class));
    }

    @Test
    void updateOrder_OrderNotFound_ThrowsException() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrder("user", 999L, new Order()));
    }

    @Test
    void updateOrder_UnauthorizedUser_ThrowsException() {
        User otherUser = new User();
        otherUser.setId(3L);
        otherUser.setUsername("otherUser");
        otherUser.setRole(User.Role.USER);

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(otherUser));

        assertThrows(AccessDeniedException.class, () -> orderService.updateOrder("otherUser", 1L, new Order()));
    }

    @Test
    void getOrders_AsUser_ReturnsUserOrders() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(orderRepository.findByUserAndStatusAndPriceRange(eq(user), any(), any(), any()))
                .thenReturn(List.of(order));

        List<Order> result = orderService.getOrders("user", null, null, null);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(order.getOrderId(), result.get(0).getOrderId());
    }

    @Test
    void getOrders_AsAdmin_ReturnsAllOrders() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(orderRepository.findByStatusAndPriceRange(any(), any(), any()))
                .thenReturn(Arrays.asList(order, new Order()));

        List<Order> result = orderService.getOrders("admin", null, null, null);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    void getOrder_AsUser_OwnOrder_Success() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrder("user", 1L);

        assertNotNull(result);
        assertEquals(order.getOrderId(), result.getOrderId());
    }

    @Test
    void getOrder_AsUser_OtherUserOrder_ThrowsException() {
        User otherUser = new User();
        otherUser.setId(3L);
        otherUser.setUsername("otherUser");
        otherUser.setRole(User.Role.USER);

        Order otherOrder = new Order();
        otherOrder.setOrderId(2L);
        otherOrder.setUser(otherUser);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(orderRepository.findById(2L)).thenReturn(Optional.of(otherOrder));

        assertThrows(AccessDeniedException.class, () -> orderService.getOrder("user", 2L));
    }

    @Test
    void getOrder_AsAdmin_AnyOrder_Success() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrder("admin", 1L);

        assertNotNull(result);
        assertEquals(order.getOrderId(), result.getOrderId());
    }

    @Test
    void deleteOrder_ExistingOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertDoesNotThrow(() -> orderService.deleteOrder(1L));
        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrder_NonExistingOrder_ThrowsException() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(999L));
    }
}



