package com.sysdesign.banking.dto;

import com.sysdesign.banking.model.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record AccountResponse(
        Long id,
        Integer userId,
        String accountNumber,
        AccountType accountType,
        BigDecimal balance,
        String currency,
        Boolean isLocked,
        LocalDateTime createdAt
) {}
