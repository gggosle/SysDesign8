package com.sysdesign.banking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.Map;


@Component
@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex
    ) {
        long startTime = (long) request.getAttribute("startTime");
        long durationMs = System.currentTimeMillis() - startTime;

        Map<String, Object> logEntry = Map.of(
                "timestamp", Instant.now().toString(),
                "method", request.getMethod(),
                "path", request.getRequestURI(),
                "status", response.getStatus(),
                "duration_ms", durationMs,
                "server_id", System.getenv().getOrDefault("SERVER_ID", "unknown")
        );
        try {
            log.info(new ObjectMapper().writeValueAsString(logEntry));
        } catch ( com.fasterxml.jackson.core.JsonProcessingException e){
            log.error("Failed to log request", e);
        }


        response.setHeader("X-Response-Time", durationMs + "ms");
        response.setHeader("X-Server-Id",
                System.getenv().getOrDefault("SERVER_ID", "unknown"));
    }
}
