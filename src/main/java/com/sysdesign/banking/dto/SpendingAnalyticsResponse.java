package com.sysdesign.banking.dto;
import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO з результатами аналітики витрат: загальна сума та розподіл по типах транзакцій.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SpendingAnalyticsResponse {
    private BigDecimal totalSpent;
    private Map<String, BigDecimal> byType; // e.g., TRANSFER, PAYMENT
}