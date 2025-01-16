package com.example.ordermanagement.presentation.controller;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MetricsController {

    private final MeterRegistry meterRegistry;

    public MetricsController(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/metrics")
    public Map<String, Double> getMetrics() {
        Map<String, Double> metrics = new HashMap<>();
        meterRegistry.getMeters().forEach(meter -> {
            metrics.put(meter.getId().getName(), meter.measure().iterator().next().getValue());
        });
        return metrics;
    }
}

