package com.example.ordermanagement.presentation.controller;

        import org.springframework.http.ResponseEntity;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.web.bind.annotation.PostMapping;
        import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<String> login() {
        // Проверяем, что объект authentication не null и пользователь аутентифицирован
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok("User authenticated: " + authentication.getName());
        } else {
            return ResponseEntity.status(401).body("Authentication failed");
        }
    }
}


