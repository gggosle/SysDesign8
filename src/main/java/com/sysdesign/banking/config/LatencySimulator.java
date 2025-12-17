package com.sysdesign.banking.config;

public class LatencySimulator {

    public static void simulateDbLatency(String operationType) {
        try {
            if ("read".equals(operationType)) {
                Thread.sleep(50);
            } else {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
