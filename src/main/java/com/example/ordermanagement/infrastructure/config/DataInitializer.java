package com.example.ordermanagement.infrastructure.config;

import com.example.ordermanagement.domain.model.Order;
import com.example.ordermanagement.domain.model.Product;
import com.example.ordermanagement.domain.model.User;
import com.example.ordermanagement.domain.repository.OrderRepository;
import com.example.ordermanagement.domain.repository.ProductRepository;
import com.example.ordermanagement.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
@Profile("!test")
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
                                      OrderRepository orderRepository,
                                      ProductRepository productRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // Создаем пользователя с ролью USER
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole(User.Role.USER);
            userRepository.save(user);

            // Создаем пользователя с ролью ADMIN
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);

            // Создаем тестовый заказ для админа
            Order order = new Order();
            order.setUser(admin);
            order.setCustomerName("Admin Test Order");
            order.setStatus(Order.OrderStatus.PENDING);
            order.setTotalPrice(new BigDecimal("100.00"));
            orderRepository.save(order);

            // Создаем тестовый продукт для заказа админа
            Product product = new Product("Admin Test Product", new BigDecimal("100.00"), 1);
            product.setOrder(order);
            productRepository.save(product);
        };
    }
}




