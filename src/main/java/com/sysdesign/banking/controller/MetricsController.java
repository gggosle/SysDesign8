package com.sysdesign.banking.controller;

import com.sysdesign.banking.config.MetricsRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class MetricsController {

    private final MetricsRegistry metrics;

    public MetricsController(MetricsRegistry metrics) {
        this.metrics = metrics;
    }

    @GetMapping("/metrics")
    public Map<String, Object> metrics() {
        return Map.of(
                "total_requests", metrics.requestCounter.get(),
                "requests_per_second", metrics.calculateRps(),
                "average_response_time", metrics.avgResponseTime.get(),
                "error_rate",
                metrics.errorCounter.get() /
                        Math.max(metrics.requestCounter.get(), 1),
                "active_connections", metrics.activeConnections.get(),
                "cache_hit_rate",
                metrics.cacheEnabled
                        ? metrics.cacheHits.get() /
                        Math.max(metrics.cacheAttempts.get(), 1)
                        : 0
        );
    }
}
