package com.example.ordermanagement.presentation.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDTO {
    private Long orderId;
    private String customerName;
    private String status;
    private BigDecimal totalPrice;
    private List<ProductDTO> products;
}