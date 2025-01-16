package com.example.ordermanagement.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
    List<Order> findByStatusAndPriceRange(Order.OrderStatus status, BigDecimal minPrice, BigDecimal maxPrice);
    void deleteById(Long id);
}