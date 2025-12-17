package com.sysdesign.banking.controller;

import com.sysdesign.banking.config.MetricsRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;


@RestController
public class HealthController {

    private final long serverStartTime = System.currentTimeMillis();
    private final MetricsRegistry metricsRegistry;

    public HealthController(MetricsRegistry metricsRegistry) {
        this.metricsRegistry = metricsRegistry;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "healthy",
                "timestamp", Instant.now().toString(),
                "server_id", System.getenv("SERVER_ID"),
                "uptime_seconds", (System.currentTimeMillis() - serverStartTime) / 1000,
                "request_count", metricsRegistry.requestCounter.get()
        );
    }
}