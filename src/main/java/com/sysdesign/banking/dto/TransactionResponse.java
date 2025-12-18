package com.sysdesign.banking.dto;

import com.sysdesign.banking.model.TransactionStatus;
import com.sysdesign.banking.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for exposing Transaction data through the API.
 */
public record TransactionResponse(
        Long id,
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount,
        TransactionType type,
        TransactionStatus status,
        String description,
        String referenceNumber,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {}

