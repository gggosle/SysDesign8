package com.sysdesign.banking.controller;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Невеликий контролер для включення "chaos mode" під час тестування/демонстрацій.
 * Використовуйте обережно — змінює глобальний стан застосунку.
 */
@RestController
@RequestMapping("/api/chaos")
public class ChaosController {

    @Getter
    private static volatile boolean chaosEnabled = false;

    @GetMapping("/enable")
    public Map<String, String> enableChaos() {
        chaosEnabled = true;
        return Map.of("message", "Chaos mode enabled");
    }
}