package com.sysdesign.banking.dto;

import com.sysdesign.banking.model.TransactionStatus;
import com.sysdesign.banking.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO для повернення інформації про транзакцію через API.
 *
 * Поля:
 * - id: ідентифікатор транзакції
 * - fromAccountId / toAccountId: ідентифікатори рахунків (nullable, якщо відсутній)
 * - amount: сума операції
 * - type/status: тип і статус транзакції
 * - description/referenceNumber: опис та унікальний референс
 * - createdAt/completedAt: часові позначки
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
