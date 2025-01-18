package com.example.ordermanagement.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    private boolean deleted = false;

    public Order(long l, String janeDoe, OrderStatus confirmed, BigDecimal valueOf, List<Product> products2) {
    }

    public enum OrderStatus {
        PENDING, CONFIRMED, CANCELLED
    }

    public void updateProducts(List<Product> newProducts) {
        if (newProducts == null) {
            this.products.clear();
            return;
        }

        // Обновляем существующие продукты и добавляем новые
        for (int i = 0; i < newProducts.size(); i++) {
            Product newProduct = newProducts.get(i);
            if (i < this.products.size()) {
                // Обновляем существующий продукт
                Product existingProduct = this.products.get(i);
                existingProduct.setName(newProduct.getName());
                existingProduct.setPrice(newProduct.getPrice());
                existingProduct.setQuantity(newProduct.getQuantity());
            } else {
                // Добавляем новый продукт
                this.products.add(newProduct);
            }
        }

        // Удаляем лишние продукты, если новый список короче
        while (this.products.size() > newProducts.size()) {
            this.products.remove(this.products.size() - 1);
        }
    }


    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customerName='" + customerName + '\'' +
                ", status=" + status +
                ", totalPrice=" + totalPrice +
                ", productsCount=" + (products != null ? products.size() : 0) +
                ", deleted=" + deleted +
                '}';
    }
}










