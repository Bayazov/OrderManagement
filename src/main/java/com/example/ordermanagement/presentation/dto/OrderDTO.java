package com.example.ordermanagement.presentation.dto;

import com.example.ordermanagement.domain.model.Order;
import com.example.ordermanagement.domain.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderDTO {
    private Long orderId;

    @NotNull(message = "Customer name cannot be null")
    @NotBlank(message = "Customer name cannot be blank")
    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    private String customerName;

    @NotNull(message = "Status cannot be null")
    private String status;

    @NotNull(message = "Total price cannot be null")
    @Positive(message = "Total price must be positive")
    private BigDecimal totalPrice;

    @NotNull(message = "Products cannot be null")
    @Size(min = 1, message = "Order must contain at least one product")
    private List<ProductDTO> products;

    // Convert DTO to Entity
    public static Order toEntity(OrderDTO dto) {
        if (dto == null) {
            return null;
        }

        Order order = new Order();
        order.setOrderId(dto.getOrderId());
        order.setCustomerName(dto.getCustomerName());
        if (dto.getStatus() != null) {
            order.setStatus(Order.OrderStatus.valueOf(dto.getStatus().toUpperCase()));
        }
        order.setTotalPrice(dto.getTotalPrice());

        if (dto.getProducts() != null) {
            List<Product> products = dto.getProducts().stream()
                    .map(ProductDTO::toEntity)
                    .collect(Collectors.toList());
            order.setProducts(products);
        }

        return order;
    }

    // Convert Entity to DTO
    public static OrderDTO fromEntity(Order entity) {
        if (entity == null) {
            return null;
        }

        OrderDTO dto = new OrderDTO();
        dto.setOrderId(entity.getOrderId());
        dto.setCustomerName(entity.getCustomerName());
        dto.setStatus(entity.getStatus().name());
        dto.setTotalPrice(entity.getTotalPrice());

        if (entity.getProducts() != null) {
            List<ProductDTO> productDTOs = entity.getProducts().stream()
                    .map(ProductDTO::fromEntity)
                    .collect(Collectors.toList());
            dto.setProducts(productDTOs);
        }

        return dto;
    }
}

