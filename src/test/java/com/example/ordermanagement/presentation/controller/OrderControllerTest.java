package com.example.ordermanagement.presentation.controller;

import com.example.ordermanagement.application.service.OrderService;
import com.example.ordermanagement.domain.model.Order;
import com.example.ordermanagement.domain.model.Product;
import com.example.ordermanagement.presentation.dto.OrderDTO;
import com.example.ordermanagement.presentation.dto.ProductDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_AsUser_ShouldCreateOrder() throws Exception {
        OrderDTO orderDTO = createSampleOrderDTO();
        Order order = OrderDTO.toEntity(orderDTO);
        when(orderService.createOrder(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("John Doe"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOrder_AsAdmin_ShouldDeleteOrder() throws Exception {
        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteOrder_AsUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllOrders_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getOrderById_AsUser_ShouldReturnOrder() throws Exception {
        Order order = createSampleOrder();
        when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("John Doe"));
    }

    private OrderDTO createSampleOrderDTO() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCustomerName("John Doe");
        orderDTO.setStatus("PENDING");
        orderDTO.setTotalPrice(BigDecimal.valueOf(100));

        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test Product");
        productDTO.setPrice(BigDecimal.valueOf(100));
        productDTO.setQuantity(1);

        orderDTO.setProducts(Collections.singletonList(productDTO));
        return orderDTO;
    }

    private Order createSampleOrder() {
        Order order = new Order();
        order.setOrderId(1L);
        order.setCustomerName("John Doe");
        order.setStatus(Order.OrderStatus.PENDING);
        order.setTotalPrice(BigDecimal.valueOf(100));

        Product product = new Product();
        product.setProductId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));
        product.setQuantity(1);

        order.setProducts(Collections.singletonList(product));
        return order;
    }
}


