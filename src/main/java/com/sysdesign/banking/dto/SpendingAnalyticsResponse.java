package com.sysdesign.banking.dto;
import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SpendingAnalyticsResponse {
    private BigDecimal totalSpent;
    private Map<String, BigDecimal> byType; // e.g., TRANSFER, PAYMENT
}