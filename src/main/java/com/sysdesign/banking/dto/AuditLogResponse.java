package com.sysdesign.banking.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * DTO for AuditLog entries returned by the API.
 */
public record AuditLogResponse(
        Long id,
        Long transactionId,
        String action,
        Integer userId,
        String ipAddress,
        JsonNode details,
        LocalDateTime timestamp
) {}

