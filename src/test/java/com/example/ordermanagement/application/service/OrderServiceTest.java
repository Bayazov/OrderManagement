package com.example.ordermanagement.application.service;

import com.example.ordermanagement.domain.event.OrderStatusChangedEvent;
import com.example.ordermanagement.domain.exception.InvalidOrderException;
import com.example.ordermanagement.domain.exception.OrderNotFoundException;
import com.example.ordermanagement.domain.exception.TotalPriceMismatchException;
import com.example.ordermanagement.domain.model.Order;
import com.example.ordermanagement.domain.model.Product;
import com.example.ordermanagement.domain.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @ParameterizedTest
    @ValueSource(strings = {"Alice Smith", "Bob Johnson", "Charlie Brown"})
    void createOrder_ShouldSaveAndReturnOrder(String customerName) {
        // Создаем тестовый заказ
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setStatus(Order.OrderStatus.PENDING);

        // Создаем тестовый продукт
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));
        product.setQuantity(1);
        products.add(product);

        order.setProducts(products);
        order.setTotalPrice(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP));

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order createdOrder = orderService.createOrder(order);

        assertNotNull(createdOrder);
        assertEquals(customerName, createdOrder.getCustomerName());
        assertEquals(Order.OrderStatus.PENDING, createdOrder.getStatus());
        assertEquals(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP), createdOrder.getTotalPrice());
        assertNotNull(createdOrder.getProducts());
        assertEquals(1, createdOrder.getProducts().size());
        assertEquals("Test Product", createdOrder.getProducts().get(0).getName());

        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void updateOrder_ShouldUpdateAndReturnOrder() {
        Long orderId = 1L;

        // Создаем существующий заказ
        Order existingOrder = new Order();
        existingOrder.setOrderId(orderId);
        existingOrder.setCustomerName("Existing Customer");
        existingOrder.setStatus(Order.OrderStatus.PENDING);
        existingOrder.setTotalPrice(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP));
        List<Product> existingProducts = new ArrayList<>();
        Product existingProduct = new Product();
        existingProduct.setName("Existing Product");
        existingProduct.setPrice(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP));
        existingProduct.setQuantity(1);
        existingProducts.add(existingProduct);
        existingOrder.setProducts(existingProducts);

        // Создаем обновленный заказ
        Order updatedOrder = new Order();
        updatedOrder.setOrderId(orderId);
        updatedOrder.setCustomerName("Updated Customer");
        updatedOrder.setStatus(Order.OrderStatus.CONFIRMED);
        updatedOrder.setTotalPrice(BigDecimal.valueOf(150).setScale(2, BigDecimal.ROUND_HALF_UP));
        List<Product> updatedProducts = new ArrayList<>();
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(BigDecimal.valueOf(150).setScale(2, BigDecimal.ROUND_HALF_UP));
        updatedProduct.setQuantity(1);
        updatedProducts.add(updatedProduct);
        updatedOrder.setProducts(updatedProducts);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        Order result = orderService.updateOrder(orderId, updatedOrder);

        assertNotNull(result);
        assertEquals("Updated Customer", result.getCustomerName());
        assertEquals(Order.OrderStatus.CONFIRMED, result.getStatus());
        assertEquals(BigDecimal.valueOf(150).setScale(2, BigDecimal.ROUND_HALF_UP), result.getTotalPrice());

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(eventPublisher, times(1)).publishEvent(any(OrderStatusChangedEvent.class));
    }

    @Test
    void getOrders_ShouldReturnFilteredOrders() {
        Order.OrderStatus status = Order.OrderStatus.PENDING;
        BigDecimal minPrice = BigDecimal.valueOf(50);
        BigDecimal maxPrice = BigDecimal.valueOf(150);

        // Создаем тестовые заказы с продуктами
        List<Product> products1 = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setPrice(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP));
        product1.setQuantity(1);
        products1.add(product1);

        List<Product> products2 = new ArrayList<>();
        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setPrice(BigDecimal.valueOf(200).setScale(2, BigDecimal.ROUND_HALF_UP));
        product2.setQuantity(1);
        products2.add(product2);

        List<Order> orders = Arrays.asList(
                new Order(1L, "Customer 1", Order.OrderStatus.PENDING, BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP), products1, false),
                new Order(2L, "Customer 2", Order.OrderStatus.CONFIRMED, BigDecimal.valueOf(200).setScale(2, BigDecimal.ROUND_HALF_UP), products2, false)
        );

        when(orderRepository.findByStatusAndPriceRange(status, minPrice, maxPrice)).thenReturn(orders);

        List<Order> result = orderService.getOrders(status, minPrice, maxPrice);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Customer 1", result.get(0).getCustomerName());
        assertEquals("Customer 2", result.get(1).getCustomerName());

        verify(orderRepository, times(1)).findByStatusAndPriceRange(status, minPrice, maxPrice);
    }

    @Test
    void getOrderById_ShouldReturnOrder() {
        Long orderId = 1L;

        // Создаем тестовый заказ с продуктом
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP));
        product.setQuantity(1);
        products.add(product);

        Order order = new Order(orderId, "Test Customer", Order.OrderStatus.PENDING, BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP), products, false);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals("Test Customer", result.getCustomerName());
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());
        assertEquals(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP), result.getTotalPrice());

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void deleteOrder_ShouldCancelOrder() {
        Long orderId = 1L;

        // Создаем тестовый заказ с продуктом
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP));
        product.setQuantity(1);
        products.add(product);

        Order order = new Order(orderId, "Test Customer", Order.OrderStatus.PENDING, BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP), products, false);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.deleteOrder(orderId);

        assertEquals(Order.OrderStatus.CANCELLED, order.getStatus());

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void updateOrder_WithMismatchedTotalPrice_ShouldThrowException() {
        Long orderId = 1L;

        Order existingOrder = new Order();
        existingOrder.setOrderId(orderId);
        existingOrder.setCustomerName("Test Customer");
        existingOrder.setStatus(Order.OrderStatus.PENDING);
        existingOrder.setTotalPrice(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP));

        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setPrice(BigDecimal.valueOf(50).setScale(2, BigDecimal.ROUND_HALF_UP));
        product1.setQuantity(1);
        products.add(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setPrice(BigDecimal.valueOf(60).setScale(2, BigDecimal.ROUND_HALF_UP));
        product2.setQuantity(1);
        products.add(product2);

        existingOrder.setProducts(products);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        Order updatedOrder = new Order();
        updatedOrder.setOrderId(orderId);
        updatedOrder.setCustomerName("Updated Customer");
        updatedOrder.setStatus(Order.OrderStatus.CONFIRMED);
        updatedOrder.setTotalPrice(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_HALF_UP)); // Неправильная общая сумма
        updatedOrder.setProducts(products);

        assertThrows(TotalPriceMismatchException.class, () -> orderService.updateOrder(orderId, updatedOrder));
    }

    @Test
    void createOrder_WithInvalidData_ShouldThrowException() {
        Order invalidOrder = new Order();
        // Не устанавливаем customerName, что должно вызвать исключение

        assertThrows(InvalidOrderException.class, () -> orderService.createOrder(invalidOrder));
    }

    @Test
    void getOrderById_WithNonExistentId_ShouldThrowException() {
        Long nonExistentId = 999L;

        when(orderRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(nonExistentId));
    }
}






