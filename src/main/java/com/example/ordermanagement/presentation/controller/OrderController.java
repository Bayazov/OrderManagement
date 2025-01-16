package com.example.ordermanagement.presentation.controller;

import com.example.ordermanagement.application.service.OrderService;
import com.example.ordermanagement.domain.model.Order;
import com.example.ordermanagement.presentation.dto.OrderDTO;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/orders")
@Timed("orders")
@Tag(name = "Order", description = "Order management API")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Create a new order", responses = {
            @ApiResponse(responseCode = "200", description = "Order created successfully", content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid order data")
    })
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        // Создаем новый заказ
        Order order = orderService.createOrder(OrderDTO.toEntity(orderDTO));
        return ResponseEntity.ok(OrderDTO.fromEntity(order));
    }

    @PutMapping("/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Update an existing order", responses = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully", content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid order data"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long orderId, @Valid @RequestBody OrderDTO orderDTO) {
        // Обновляем существующий заказ
        Order order = orderService.updateOrder(orderId, OrderDTO.toEntity(orderDTO));
        return ResponseEntity.ok(OrderDTO.fromEntity(order));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get all orders", responses = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully", content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<List<OrderDTO>> getOrders(
            @Parameter(description = "Order status") @RequestParam(required = false) String status,
            @Parameter(description = "Minimum order price") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum order price") @RequestParam(required = false) BigDecimal maxPrice) {
        // Получаем список заказов с фильтрацией
        Order.OrderStatus orderStatus = status != null ? Order.OrderStatus.valueOf(status.toUpperCase()) : null;
        List<Order> orders = orderService.getOrders(orderStatus, minPrice, maxPrice);
        return ResponseEntity.ok(orders.stream().map(OrderDTO::fromEntity).toList());
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get an order by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully", content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        // Получаем заказ по ID
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(OrderDTO.fromEntity(order));
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an order by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        // Удаляем заказ по ID
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}



