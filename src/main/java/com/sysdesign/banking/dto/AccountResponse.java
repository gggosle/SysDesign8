package com.sysdesign.banking.dto;

import com.sysdesign.banking.model.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record AccountResponse(
        Long id,
        Integer userId,
        AccountType accountType,
        Boolean isLocked,
        String accountNumber,
        BigDecimal balance,
        String currency,
        LocalDateTime createdAt
) {}
