package com.example.ordermanagement.presentation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void login_WithValidCredentials_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string("User authenticated: user"));
    }

    @Test
    void login_WithoutCredentials_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/login"))
                .andExpect(status().isUnauthorized());
    }
}
