package com.example.ordermanagement.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long productId;
    @NotNull(message = "Product name cannot be null")
    @NotBlank(message = "Product name cannot be blank")
    @Size(max = 100, message = "Product name must not exceed 100 characters")
    private String name;
    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be positive")
    private int quantity;
}

