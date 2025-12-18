package com.sysdesign.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysdesign.banking.config.WebConfig;
import com.sysdesign.banking.dto.LockRequest;
import com.sysdesign.banking.dto.RecurringSetupRequest;
import com.sysdesign.banking.dto.SpendingAnalyticsResponse;
import com.sysdesign.banking.model.Account;
import com.sysdesign.banking.model.AccountType;
import com.sysdesign.banking.model.RecurringPayment;
import com.sysdesign.banking.model.Transaction;
import com.sysdesign.banking.repo.AccountRepository;
import com.sysdesign.banking.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Import(WebConfig.class)
class AccountControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private RecurringService recurringService;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setup() {
        Account account = new Account();
        account.setUserId(42);
        account.setAccountNumber("ACC-123");
        account.setBalance(BigDecimal.valueOf(1000));
        account.setAccountType(AccountType.checking);
        accountRepository.save(account);
    }

    @Test
    void listAccountsIntegration() throws Exception {
        mockMvc.perform(get("/api/accounts")
                        .header("X-User-Id", 42)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].accountNumber").value("ACC-123"))
                .andExpect(jsonPath("$[0].balance").value(1000));
    }

    @Test
    void getBalance() throws Exception {
        when(accountService.getBalance(1L))
                .thenReturn(BigDecimal.valueOf(123.45));

        mockMvc.perform(get("/api/accounts/1/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("123.45"));
    }

    @Test
    void getTransactions() throws Exception {
        Transaction tx = new Transaction();
        tx.setId(10L);

        when(transactionService.getTransactionsForAccount(1L, 0, 20))
                .thenReturn(List.of(tx));

        mockMvc.perform(get("/api/accounts/1/transactions")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

//    @Test
//    void lockAccount() throws Exception {
//        LockRequest req = new LockRequest(true);
//        Account locked = new Account();
//        locked.setIsLocked(true);
//
//        when(accountService.lockOrUnlock(1L, true))
//                .thenReturn(locked);
//
//        mockMvc.perform(post("/api/accounts/1/lock")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.locked").value(true));
//    }

    @Test
    void setupRecurring() throws Exception {
        RecurringSetupRequest req = new RecurringSetupRequest();
        RecurringPayment payment = new RecurringPayment();
        payment.setId(5L);

        when(recurringService.setup(any()))
                .thenReturn(payment);

        mockMvc.perform(post("/api/recurring/setup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void getStatement() throws Exception {
        mockMvc.perform(get("/api/statements/2025-12")
                        .header("X-User-Id", 42))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("2025-12"))
                .andExpect(jsonPath("$.transactions").isArray());
    }

//    @Test
//    void analytics() throws Exception {
//        SpendingAnalyticsResponse resp =
//                new SpendingAnalyticsResponse(BigDecimal.TEN);
//
//        when(analyticsService.spending(42))
//                .thenReturn(resp);
//
//        mockMvc.perform(get("/api/analytics/spending")
//                        .header("X-User-Id", 42))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.total").value(10));
//    }
}