package com.sysdesign.banking.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class MetricsRegistry {

    public AtomicLong requestCounter = new AtomicLong();
    public AtomicLong errorCounter = new AtomicLong();
    public AtomicLong activeConnections = new AtomicLong();
    public AtomicLong cacheHits = new AtomicLong();
    public AtomicLong cacheAttempts = new AtomicLong();
    public AtomicReference<Double> avgResponseTime = new AtomicReference<>(0.0);

    public boolean cacheEnabled = true;

    public double calculateRps() {
        return requestCounter.get() / 60.0;
    }
}
