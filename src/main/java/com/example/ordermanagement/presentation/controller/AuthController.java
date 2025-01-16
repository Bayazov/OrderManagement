package com.example.ordermanagement.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<String> login(Authentication authentication) {
        // Генерация JWT токен
        return ResponseEntity.ok("User authenticated: " + authentication.getName());
    }
}
