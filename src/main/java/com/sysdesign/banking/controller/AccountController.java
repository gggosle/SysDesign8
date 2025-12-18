package com.sysdesign.banking.controller;

import com.sysdesign.banking.dto.*;
import com.sysdesign.banking.model.*;
import com.sysdesign.banking.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final RecurringService recurringService;
    private final AnalyticsService analyticsService;


    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> listAccounts(@RequestHeader("X-User-Id") Integer userId) {
        return ResponseEntity.ok(accountService.listByUser(userId));
    }

    @GetMapping("/accounts/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getBalance(id));
    }

    @GetMapping("/accounts/{id}/transactions")
    public ResponseEntity<List<Transaction>> transactions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(transactionService.getTransactionsForAccount(id, page, size));
    }

    @PostMapping("/accounts/{id}/lock")
    public ResponseEntity<AccountResponse> lockAccount(@PathVariable Long id, @RequestBody LockRequest req) {
        AccountResponse a = accountService.lockOrUnlock(id, req.isLock());
        return ResponseEntity.ok(a);
    }

    @PostMapping("/recurring/setup")
    public ResponseEntity<RecurringPayment> setupRecurring(@RequestBody RecurringSetupRequest req) {
        return ResponseEntity.ok(recurringService.setup(req));
    }

    @GetMapping("/statements/{month}")
    public ResponseEntity<StatementResponse> statement(@PathVariable String month, @RequestHeader("X-User-Id") Integer userId) {
        // month = "2025-12" simple implementation: fetch transactions for user's accounts for that month
        // For brevity return an empty wrapper or implement via SQL date filters
        StatementResponse resp = StatementResponse.builder().month(month).transactions(List.of()).build();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/analytics/spending")
    public ResponseEntity<SpendingAnalyticsResponse> analytics(@RequestHeader("X-User-Id") Integer userId) {
        return ResponseEntity.ok(analyticsService.spending(userId));
    }
}
