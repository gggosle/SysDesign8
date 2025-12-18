package com.sysdesign.banking.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.JsonNode;

public record AuditLogResponse(
        Long id,
        Long transactionId,
        String action,
        Integer userId,
        String ipAddress,
        JsonNode details,
        LocalDateTime timestamp
) {}

