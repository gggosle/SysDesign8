package com.sysdesign.banking.dto.mapper;

import com.sysdesign.banking.dto.TransactionResponse;
import com.sysdesign.banking.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "fromAccountId", expression = "java(transaction.getFromAccount() == null ? null : transaction.getFromAccount().getId())")
    @Mapping(target = "toAccountId", expression = "java(transaction.getToAccount() == null ? null : transaction.getToAccount().getId())")
    TransactionResponse toDto(Transaction transaction);
}

