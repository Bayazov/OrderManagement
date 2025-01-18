package com.example.ordermanagement.presentation.dto;

import com.example.ordermanagement.domain.model.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "Product Data Transfer Object")
public class ProductDTO {
    @Schema(hidden = true)
    @JsonIgnore
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

    public static Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());

        return product;
    }

    public static ProductDTO fromEntity(Product entity) {
        if (entity == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setProductId(entity.getProductId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setQuantity(entity.getQuantity());

        return dto;
    }
}






