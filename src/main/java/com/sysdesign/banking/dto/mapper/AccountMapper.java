package com.sysdesign.banking.dto.mapper;


import com.sysdesign.banking.dto.AccountResponse;
import com.sysdesign.banking.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountResponse toDto(Account account);
}