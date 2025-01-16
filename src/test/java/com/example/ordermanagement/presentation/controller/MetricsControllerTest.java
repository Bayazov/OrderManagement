package com.example.ordermanagement.presentation.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        if (meterRegistry == null) {
            meterRegistry = new SimpleMeterRegistry();
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getMetrics_AsAdmin_ShouldReturnMetrics() throws Exception {
        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMetrics_AsUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/metrics"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMetrics_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/metrics"))
                .andExpect(status().isUnauthorized());
    }
}

