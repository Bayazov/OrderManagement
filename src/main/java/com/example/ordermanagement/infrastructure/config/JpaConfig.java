package com.example.ordermanagement.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.ordermanagement.infrastructure")
@Profile("!test")
public class JpaConfig {
}