package com.sysdesign.banking.service;

import com.sysdesign.banking.model.Account;
import com.sysdesign.banking.repo.AccountRepository;
import com.sysdesign.banking.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<Account> listByUser(Integer userId) {
        return accountRepository.findByUserId(userId);
    }

    public BigDecimal getBalance(Long accountId) {
        return accountRepository.findById(accountId)
                .map(Account::getBalance)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }

    @Transactional
    public Account lockOrUnlock(Long accountId, boolean lock) {
        Account acct = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        acct.setIsLocked(lock);
        return accountRepository.save(acct);
    }

    @Transactional
    public Account findAndLock(Long accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }

    @Transactional
    public void save(Account a) {
        accountRepository.save(a);
    }
}
