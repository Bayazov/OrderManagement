package com.example.ordermanagement.domain.repository;

import com.example.ordermanagement.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.deleted = false " +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND (:minPrice IS NULL OR o.totalPrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR o.totalPrice <= :maxPrice)")
    List<Order> findByStatusAndPriceRange(@Param("status") Order.OrderStatus status,
                                          @Param("minPrice") BigDecimal minPrice,
                                          @Param("maxPrice") BigDecimal maxPrice);
}


