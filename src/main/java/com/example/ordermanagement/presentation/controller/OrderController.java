package com.example.ordermanagement.presentation.controller;

import com.example.ordermanagement.application.service.OrderService;
import com.example.ordermanagement.domain.model.Order;
import com.example.ordermanagement.domain.model.Product;
import com.example.ordermanagement.presentation.dto.OrderDTO;
import com.example.ordermanagement.presentation.dto.ProductDTO;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
    @Timed(value = "orders.create", description = "Time taken to create an order")
    @Operation(summary = "Create a new order", responses = {
            @ApiResponse(responseCode = "200", description = "Order created successfully", content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid order data")
    })
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        Order order = convertToEntity(orderDTO);
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(convertToDTO(createdOrder));
    }

    @PutMapping("/{orderId}")
    @Timed(value = "orders.update", description = "Time taken to update an order")
    @Operation(summary = "Update an existing order", responses = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully", content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid order data"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long orderId, @RequestBody OrderDTO orderDTO) {
        Order order = convertToEntity(orderDTO);
        Order updatedOrder = orderService.updateOrder(orderId, order);
        return ResponseEntity.ok(convertToDTO(updatedOrder));
    }

    @GetMapping
    @Timed(value = "orders.get", description = "Time taken to get orders")
    @Operation(summary = "Get all orders", responses = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully", content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public ResponseEntity<List<OrderDTO>> getOrders(
            @Parameter(description = "Order status") @RequestParam(required = false) String status,
            @Parameter(description = "Minimum order price") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum order price") @RequestParam(required = false) BigDecimal maxPrice) {
        Order.OrderStatus orderStatus = status != null ? Order.OrderStatus.valueOf(status.toUpperCase()) : null;
        List<Order> orders = orderService.getOrders(orderStatus, minPrice, maxPrice);
        List<OrderDTO> orderDTOs = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/{orderId}")
    @Timed(value = "orders.get.by.id", description = "Time taken to get an order by id")
    @Operation(summary = "Get an order by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully", content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(convertToDTO(order));
    }

    @DeleteMapping("/{orderId}")
    @Timed(value = "orders.delete", description = "Time taken to delete an order")
    @Operation(summary = "Delete an order by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    private Order convertToEntity(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrderId(orderDTO.getOrderId());
        order.setCustomerName(orderDTO.getCustomerName());
        if (orderDTO.getStatus() != null) {
            order.setStatus(Order.OrderStatus.valueOf(orderDTO.getStatus().toUpperCase()));
        }
        order.setTotalPrice(orderDTO.getTotalPrice());
        if (orderDTO.getProducts() != null) {
            order.setProducts(orderDTO.getProducts().stream()
                    .map(this::convertToProductEntity)
                    .collect(Collectors.toList()));
        }
        return order;
    }

    private Product convertToProductEntity(ProductDTO productDTO) {
        Product product = new Product();
        product.setProductId(productDTO.getProductId());
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        return product;
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(order.getOrderId());
        orderDTO.setCustomerName(order.getCustomerName());
        orderDTO.setStatus(order.getStatus().name());
        orderDTO.setTotalPrice(order.getTotalPrice());
        if (order.getProducts() != null) {
            orderDTO.setProducts(order.getProducts().stream()
                    .map(this::convertToProductDTO)
                    .collect(Collectors.toList()));
        }
        return orderDTO;
    }

    private ProductDTO convertToProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setPrice(product.getPrice());
        productDTO.setQuantity(product.getQuantity());
        return productDTO;
    }
}





