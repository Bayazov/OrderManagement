package com.example.ordermanagement.presentation;

import com.example.ordermanagement.application.OrderService;
import com.example.ordermanagement.domain.model.Order;
import com.example.ordermanagement.presentation.dto.OrderDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderController = new OrderController(orderService);
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCustomerName("John Doe");
        orderDTO.setStatus("PENDING");
        orderDTO.setTotalPrice(BigDecimal.valueOf(100));

        Order createdOrder = new Order(1L, "John Doe", Order.OrderStatus.PENDING, BigDecimal.valueOf(100), null);

        when(orderService.createOrder(any(Order.class))).thenReturn(createdOrder);

        ResponseEntity<OrderDTO> response = orderController.createOrder(orderDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getOrderId());
        assertEquals("John Doe", response.getBody().getCustomerName());
        assertEquals("PENDING", response.getBody().getStatus());
        assertEquals(BigDecimal.valueOf(100), response.getBody().getTotalPrice());

        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    void updateOrder_ShouldReturnUpdatedOrder() {
        Long orderId = 1L;
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCustomerName("Jane Doe");
        orderDTO.setStatus("CONFIRMED");
        orderDTO.setTotalPrice(BigDecimal.valueOf(150));

        Order updatedOrder = new Order(orderId, "Jane Doe", Order.OrderStatus.CONFIRMED, BigDecimal.valueOf(150), null);

        when(orderService.updateOrder(eq(orderId), any(Order.class))).thenReturn(updatedOrder);

        ResponseEntity<OrderDTO> response = orderController.updateOrder(orderId, orderDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderId, response.getBody().getOrderId());
        assertEquals("Jane Doe", response.getBody().getCustomerName());
        assertEquals("CONFIRMED", response.getBody().getStatus());
        assertEquals(BigDecimal.valueOf(150), response.getBody().getTotalPrice());

        verify(orderService, times(1)).updateOrder(eq(orderId), any(Order.class));
    }

    @Test
    void getOrders_ShouldReturnListOfOrders() {
        String status = "PENDING";
        BigDecimal minPrice = BigDecimal.valueOf(50);
        BigDecimal maxPrice = BigDecimal.valueOf(150);

        List<Order> orders = Arrays.asList(
                new Order(1L, "John Doe", Order.OrderStatus.PENDING, BigDecimal.valueOf(100), null),
                new Order(2L, "Jane Doe", Order.OrderStatus.PENDING, BigDecimal.valueOf(120), null)
        );

        when(orderService.getOrders(Order.OrderStatus.PENDING, minPrice, maxPrice)).thenReturn(orders);

        ResponseEntity<List<OrderDTO>> response = orderController.getOrders(status, minPrice, maxPrice);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getCustomerName());
        assertEquals("Jane Doe", response.getBody().get(1).getCustomerName());

        verify(orderService, times(1)).getOrders(Order.OrderStatus.PENDING, minPrice, maxPrice);
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        Long orderId = 1L;
        Order order = new Order(orderId, "John Doe", Order.OrderStatus.PENDING, BigDecimal.valueOf(100), null);

        when(orderService.getOrderById(orderId)).thenReturn(order);

        ResponseEntity<OrderDTO> response = orderController.getOrderById(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderId, response.getBody().getOrderId());
        assertEquals("John Doe", response.getBody().getCustomerName());
        assertEquals("PENDING", response.getBody().getStatus());
        assertEquals(BigDecimal.valueOf(100), response.getBody().getTotalPrice());

        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    void deleteOrder_ShouldReturnNoContent() {
        Long orderId = 1L;

        ResponseEntity<Void> response = orderController.deleteOrder(orderId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(orderService, times(1)).deleteOrder(orderId);
    }
}

