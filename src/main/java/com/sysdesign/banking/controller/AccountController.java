package com.sysdesign.banking.controller;

import com.sysdesign.banking.dto.*;
import com.sysdesign.banking.dto.mapper.AccountMapper;
import com.sysdesign.banking.dto.mapper.TransactionMapper;
import com.sysdesign.banking.model.*;
import com.sysdesign.banking.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final RecurringService recurringService;
    private final AnalyticsService analyticsService;
    private final TransactionMapper transactionMapper;
    private final AccountMapper accountMapper;


    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> listAccounts(@RequestHeader("X-User-Id") Integer userId) {
        List<AccountResponse> accountResponses = accountService.listByUser(userId)
                .stream()
                .map(accountMapper::toDto)
                .toList();

        return ResponseEntity.ok(accountResponses);
    }

    @GetMapping("/accounts/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getBalance(id));
    }

    @GetMapping("/accounts/{id}/transactions")
    public ResponseEntity<List<TransactionResponse>> transactions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<com.sysdesign.banking.model.Transaction> txs = transactionService.getTransactionsForAccount(id, page, size);
        List<TransactionResponse> res = txs.stream().map(transactionMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/accounts/{id}/lock")
    public ResponseEntity<AccountResponse> lockAccount(@PathVariable Long id, @RequestBody LockRequest req) {
        Account a = accountService.lockOrUnlock(id, req.isLock());
        return ResponseEntity.ok(accountMapper.toDto(a));
    }

    @PostMapping("/recurring/setup")
    public ResponseEntity<RecurringPayment> setupRecurring(@RequestBody RecurringSetupRequest req) {
        return ResponseEntity.ok(recurringService.setup(req));
    }

    @GetMapping("/statements/{month}")
    public ResponseEntity<StatementResponse> statement(@PathVariable String month, @RequestHeader("X-User-Id") Integer userId) {
        YearMonth ym;
        try {
            ym = YearMonth.parse(month);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59, 999_999_999);

        List<Account> accounts = accountService.listByUser(userId);
        List<TransactionResponse> transactions = accounts.stream()
                .map(Account::getId)
                .flatMap(accId -> transactionService.getTransactionsForAccountBetween(accId, start, end).stream())
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());

        transactions.sort((a,b) -> b.createdAt().compareTo(a.createdAt()));

        StatementResponse resp = StatementResponse.builder().month(month).transactions(transactions).build();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/analytics/spending")
    public ResponseEntity<SpendingAnalyticsResponse> analytics(@RequestHeader("X-User-Id") Integer userId) {
        return ResponseEntity.ok(analyticsService.spending(userId));
    }
}
