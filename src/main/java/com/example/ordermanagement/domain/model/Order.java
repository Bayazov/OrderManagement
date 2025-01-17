package com.example.ordermanagement.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE order_id = ?")
@Where(clause = "deleted = false")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private String customerName;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal totalPrice;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<Product> products;

    private boolean deleted = false;

    // Add constructor for tests
    public Order(Long orderId, String customerName, OrderStatus status, BigDecimal totalPrice, List<Product> products) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.status = status;
        this.totalPrice = totalPrice;
        this.products = products;
        this.deleted = false;
    }

    public enum OrderStatus {
        PENDING, CONFIRMED, CANCELLED
    }
}


