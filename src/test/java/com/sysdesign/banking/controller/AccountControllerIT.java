package com.sysdesign.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysdesign.banking.config.WebConfig;
import com.sysdesign.banking.dto.LockRequest;
import com.sysdesign.banking.dto.RecurringSetupRequest;
import com.sysdesign.banking.repo.AccountRepository;
import com.sysdesign.banking.service.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
//        accountRepository.deleteAllById(List.of(42L));
//        Account account = new Account();
//        account.setUserId(42);
//        account.setAccountNumber("ACC-123");
//        account.setBalance(BigDecimal.valueOf(1000));
//        account.setAccountType(AccountType.checking);
//        accountRepository.save(account);
    }

    @Test
    void listAccountsIntegration() throws Exception {
        mockMvc.perform(get("/api/accounts")
                        .header("X-User-Id", 7004)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void getBalance() throws Exception {
        mockMvc.perform(get("/api/accounts/1/balance"))
                .andExpect(status().isOk());
    }

    @Test
    void getTransactions() throws Exception {
        mockMvc.perform(get("/api/accounts/1/transactions")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk());
    }

    @Test
    void lockAccount() throws Exception {
        LockRequest req = new LockRequest(true, 1);

        mockMvc.perform(post("/api/accounts/1/lock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isLocked").value(true));
    }

    @Test
    void setupRecurring() throws Exception {
        RecurringSetupRequest req = RecurringSetupRequest.builder()
                .accountId(1L)
                .amount(new BigDecimal("1000"))
                .frequency("monthly")
                .nextPaymentDate(LocalDate.of(2025, 12, 4))
                .recipientAccount("5005")
                .build();

        mockMvc.perform(post("/api/recurring/setup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void getStatement() throws Exception {
        mockMvc.perform(get("/api/statements/2025-12")
                        .header("X-User-Id", 42))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value("2025-12"));
    }

    @Test
    void analytics() throws Exception {
        mockMvc.perform(get("/api/analytics/spending")
                        .header("X-User-Id", 42))
                .andExpect(status().isOk());
    }
}