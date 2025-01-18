package com.example.ordermanagement.domain.repository;

import com.example.ordermanagement.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {  // Changed from Integer to Long
}

