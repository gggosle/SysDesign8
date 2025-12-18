package com.sysdesign.banking.service;

import com.sysdesign.banking.dto.AuditLogResponse;
import com.sysdesign.banking.dto.TransactionResponse;
import com.sysdesign.banking.dto.TransferRequest;
import com.sysdesign.banking.dto.PaymentRequest;
import com.sysdesign.banking.dto.mapper.AuditLogMapper;
import com.sysdesign.banking.dto.mapper.TransactionMapper;
import com.sysdesign.banking.model.*;
import com.sysdesign.banking.repo.*;
import com.sysdesign.banking.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final AccountRepository accountRepository;
    private final RecurringPaymentRepository recurringRepo;
    private final TransactionMapper transactionMapper;
    private final AuditLogMapper auditLogMapper;

    /**
     * Transfer money between two accounts with ACID guarantees.
     * We obtain PESSIMISTIC_WRITE locks on the involved accounts in a consistent order
     * (lower id first) to prevent deadlocks.
     */
    @Transactional(rollbackFor = Exception.class)
    public TransactionResponse transfer(TransferRequest req) {
        if (req.getFromAccountId().equals(req.getToAccountId())) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }
        if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Long a1 = req.getFromAccountId();
        Long a2 = req.getToAccountId();

        Account first = accountService.findAndLock(Math.min(a1, a2));
        Account second = accountService.findAndLock(Math.max(a1, a2));

        Account from = a1.equals(first.getId()) ? first : second;
        Account to = a2.equals(first.getId()) ? first : second;

        if (Boolean.TRUE.equals(from.getIsLocked()) || Boolean.TRUE.equals(to.getIsLocked())) {
            throw new AccountLockedException("One of accounts is locked");
        }

        if (from.getBalance().compareTo(req.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(req.getAmount()));
        to.setBalance(to.getBalance().add(req.getAmount()));

        accountRepository.save(from);
        accountRepository.save(to);

        Transaction tx = Transaction.builder()
                .fromAccount(from)
                .toAccount(to)
                .amount(req.getAmount())
                .type(TransactionType.transfer)
                .status(TransactionStatus.completed)
                .description(req.getDescription())
                .referenceNumber(generateReference())
                .createdAt(LocalDateTime.now())
                .build();

        tx = transactionRepository.save(tx);

        AuditLog audit = AuditLog.builder()
                .transaction(tx)
                .action("TRANSFER")
                .userId(req.getUserId())
                .ipAddress(null)
                .details(null)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(audit);

        return transactionMapper.toDto(tx);
    }


    @Transactional(rollbackFor = Exception.class)
    public TransactionResponse payment(PaymentRequest req) {
        if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        Long id = req.getFromAccountId();

        Account acct = accountService.findAndLock(id);

        if (Boolean.TRUE.equals(acct.getIsLocked())) {
            throw new AccountLockedException("Account is locked");
        }

        if (acct.getBalance().compareTo(req.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        acct.setBalance(acct.getBalance().subtract(req.getAmount()));
        accountRepository.save(acct);

        Transaction tx = Transaction.builder()
                .fromAccount(acct)
                .toAccount(null)
                .amount(req.getAmount())
                .type(TransactionType.payment)
                .status(TransactionStatus.completed)
                .description(req.getDescription())
                .referenceNumber(generateReference())
                .createdAt(LocalDateTime.now())
                .build();

        tx = transactionRepository.save(tx);

        AuditLog audit = AuditLog.builder()
                .transaction(tx)
                .action("PAYMENT")
                .userId(req.getUserId())
                .ipAddress(null)
                .details(null)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(audit);

        return transactionMapper.toDto(tx);
    }

    private String generateReference() {
        return "TX-" + System.currentTimeMillis();
    }

    public List<Transaction> getTransactionsForAccount(Long accountId, int page, int size) {
        return transactionRepository.findByFromAccountIdOrderByCreatedAtDesc(accountId, PageRequest.of(page, size));
    }

    public List<AuditLogResponse> getAuditTrail(Long txId) {
        return auditLogRepository.findByTransactionIdOrderByTimestampAsc(txId)
                .stream()
                .map(auditLogMapper::toDto)
                .collect(Collectors.toList());
    }
}
