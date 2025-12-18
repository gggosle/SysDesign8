package com.sysdesign.banking.dto.mapper;

import com.sysdesign.banking.dto.AuditLogResponse;
import com.sysdesign.banking.model.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "transactionId", expression = "java(audit.getTransaction() == null ? null : audit.getTransaction().getId())")
    AuditLogResponse toDto(AuditLog audit);
}

