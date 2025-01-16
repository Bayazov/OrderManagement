package com.example.ordermanagement.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

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
}

