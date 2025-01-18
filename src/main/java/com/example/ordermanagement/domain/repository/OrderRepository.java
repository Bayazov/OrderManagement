package com.example.ordermanagement.domain.repository;

import com.example.ordermanagement.domain.model.Order;
import com.example.ordermanagement.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    Optional<Order> findById(Long id);
    @Query("SELECT o FROM Order o WHERE o.user = :user " +
            "AND (:status IS NULL OR o.status = :status) " +
            "AND (:minPrice IS NULL OR o.totalPrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR o.totalPrice <= :maxPrice)")
    List<Order> findByUserAndStatusAndPriceRange(@Param("user") User user,
                                                 @Param("status") Order.OrderStatus status,
                                                 @Param("minPrice") BigDecimal minPrice,
                                                 @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT o FROM Order o WHERE " +
            "(:status IS NULL OR o.status = :status) " +
            "AND (:minPrice IS NULL OR o.totalPrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR o.totalPrice <= :maxPrice)")
    List<Order> findByStatusAndPriceRange(@Param("status") Order.OrderStatus status,
                                          @Param("minPrice") BigDecimal minPrice,
                                          @Param("maxPrice") BigDecimal maxPrice);
}




