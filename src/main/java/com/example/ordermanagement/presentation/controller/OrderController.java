package com.example.ordermanagement.presentation.controller;

import com.example.ordermanagement.application.service.OrderService;
import com.example.ordermanagement.domain.model.Order;
import com.example.ordermanagement.presentation.dto.OrderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/orders")
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Создать новый заказ
    // Этот метод обрабатывает POST-запросы для создания нового заказа.
    // Он доступен как пользователям, так и администраторам.
    // Метод принимает данные заказа в формате DTO, преобразует их в сущность Order,
    // сохраняет заказ через orderService и возвращает созданный заказ в формате DTO.
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Create a new order",
            security = @SecurityRequirement(name = "basicAuth"),
            tags = {"User Operations", "Admin Operations"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order created successfully",
                            content = @Content(schema = @Schema(implementation = OrderDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid order data")
            })
    public ResponseEntity<OrderDTO> createOrder(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody OrderDTO orderDTO) {
        Order order = orderService.createOrder(userDetails.getUsername(), OrderDTO.toEntity(orderDTO));
        return ResponseEntity.ok(OrderDTO.fromEntity(order));
    }

    // Обновить существующий заказ
    // Этот метод обрабатывает PUT-запросы для обновления существующего заказа.
    // Он доступен как пользователям, так и администраторам.
    // Метод принимает ID заказа и обновленные данные в формате DTO,
    // обновляет заказ через orderService и возвращает обновленный заказ в формате DTO.
    // Если заказ не найден или пользователь не имеет прав на его обновление, будет выброшено исключение.
    @PutMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Update an existing order",
            security = @SecurityRequirement(name = "basicAuth"),
            tags = {"User Operations", "Admin Operations"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order updated successfully",
                            content = @Content(schema = @Schema(implementation = OrderDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid order data"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            })
    public ResponseEntity<OrderDTO> updateOrder(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long orderId, @Valid @RequestBody OrderDTO orderDTO) {
        Order order = orderService.updateOrder(userDetails.getUsername(), orderId, OrderDTO.toEntity(orderDTO));
        return ResponseEntity.ok(OrderDTO.fromEntity(order));
    }

    // Получить список заказов
    // Этот метод обрабатывает GET-запросы для получения списка заказов.
    // Он доступен как пользователям, так и администраторам.
    // Метод поддерживает фильтрацию по статусу заказа и диапазону цен.
    // Для пользователей возвращаются только их собственные заказы,
    // для администраторов - все заказы в системе.
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get orders",
            security = @SecurityRequirement(name = "basicAuth"),
            tags = {"User Operations", "Admin Operations"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                            content = @Content(schema = @Schema(implementation = OrderDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
            })
    public ResponseEntity<List<OrderDTO>> getOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Order status") @RequestParam(required = false) String status,
            @Parameter(description = "Minimum order price") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum order price") @RequestParam(required = false) BigDecimal maxPrice) {
        Order.OrderStatus orderStatus = status != null ? Order.OrderStatus.valueOf(status.toUpperCase()) : null;
        List<Order> orders = orderService.getOrders(userDetails.getUsername(), orderStatus, minPrice, maxPrice);
        return ResponseEntity.ok(orders.stream().map(OrderDTO::fromEntity).toList());
    }

    // Получить конкретный заказ
    // Этот метод обрабатывает GET-запросы для получения информации о конкретном заказе.
    // Он доступен как пользователям, так и администраторам.
    // Метод принимает ID заказа и возвращает информацию о нем в формате DTO.
    // Пользователи могут получить информацию только о своих заказах,
    // администраторы имеют доступ к информации о любом заказе.
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get a specific order",
            security = @SecurityRequirement(name = "basicAuth"),
            tags = {"User Operations", "Admin Operations"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order retrieved successfully",
                            content = @Content(schema = @Schema(implementation = OrderDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            })
    public ResponseEntity<OrderDTO> getOrder(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long orderId) {
        Order order = orderService.getOrder(userDetails.getUsername(), orderId);
        return ResponseEntity.ok(OrderDTO.fromEntity(order));
    }

    // Удалить заказ
    // Этот метод обрабатывает DELETE-запросы для удаления заказа.
    // Он доступен только администраторам.
    // Метод принимает ID заказа, удаляет его через orderService
    // и возвращает ответ с кодом 204 (No Content) в случае успешного удаления.
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an order",
            security = @SecurityRequirement(name = "basicAuth"),
            tags = {"Admin Operations"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            })
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    // Получить информацию о текущем пользователе
    // Этот метод обрабатывает GET-запросы для получения информации о текущем аутентифицированном пользователе.
    // Он доступен как пользователям, так и администраторам.
    // Метод возвращает имя пользователя и его роли в системе.
    // Эта информация может быть полезна для отладки и проверки правильности аутентификации.
    @GetMapping("/user-info")
    @Operation(summary = "Get current user info",
            security = @SecurityRequirement(name = "basicAuth"),
            tags = {"User Operations", "Admin Operations"})
    public ResponseEntity<String> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok("Current user: " + userDetails.getUsername() +
                ", Roles: " + userDetails.getAuthorities());
    }
}


