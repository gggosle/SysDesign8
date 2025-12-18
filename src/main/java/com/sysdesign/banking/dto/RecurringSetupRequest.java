package com.sysdesign.banking.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

/**
 * DTO для налаштування повторюваного платежу.
 * Поле frequency приймає значення: DAILY, WEEKLY, MONTHLY, YEARLY (рядок).
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecurringSetupRequest {
    private Long accountId;
    private BigDecimal amount;
    private String recipientAccount;
    private String frequency; // DAILY, WEEKLY, MONTHLY, YEARLY
    private LocalDate nextPaymentDate;
    private Integer userId;
}