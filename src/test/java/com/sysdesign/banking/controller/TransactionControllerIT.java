package com.sysdesign.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysdesign.banking.config.WebConfig;
import com.sysdesign.banking.dto.LockRequest;
import com.sysdesign.banking.dto.RecurringSetupRequest;
import com.sysdesign.banking.dto.TransferRequest;
import com.sysdesign.banking.dto.PaymentRequest;
import com.sysdesign.banking.model.Transaction;
import com.sysdesign.banking.model.AuditLog;
import com.sysdesign.banking.service.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Import(WebConfig.class)
class TransactionControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionService transactionService;

    @BeforeEach
    void setup() {
        // no DB access; transactionService is mocked
    }

    @Test
    void transferEndpointReturnsTransaction() throws Exception {
        Transaction tx = Transaction.builder()
                .id(123L)
                .amount(new BigDecimal("50.00"))
                .description("desc")
                .createdAt(LocalDateTime.of(2025,12,4,10,0))
                .build();

        when(transactionService.transfer(org.mockito.ArgumentMatchers.any()))
                .thenReturn(tx);

        TransferRequest req = TransferRequest.builder()
                .fromAccountId(1L)
                .toAccountId(2L)
                .amount(new BigDecimal("50.00"))
                .description("desc")
                .userId(42)
                .build();

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.amount").value(50.00));
    }

    @Test
    void paymentEndpointReturnsTransaction() throws Exception {
        Transaction tx = Transaction.builder()
                .id(124L)
                .amount(new BigDecimal("25.50"))
                .description("pay")
                .createdAt(LocalDateTime.of(2025,12,4,11,0))
                .build();

        when(transactionService.payment(org.mockito.ArgumentMatchers.any()))
                .thenReturn(tx);

        PaymentRequest req = PaymentRequest.builder()
                .fromAccountId(1L)
                .recipientAccount("5005")
                .amount(new BigDecimal("25.50"))
                .description("pay")
                .userId(42)
                .build();

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(124))
                .andExpect(jsonPath("$.amount").value(25.50));
    }

    @Test
    void auditEndpointReturnsList() throws Exception {
        AuditLog a1 = AuditLog.builder()
                .id(10L)
                .action("TRANSFER")
                .userId(42)
                .timestamp(LocalDateTime.of(2025,12,4,12,0))
                .build();

        when(transactionService.getAuditTrail(99L)).thenReturn(List.of(a1));

        mockMvc.perform(get("/api/audit/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].action").value("TRANSFER"));
    }
}