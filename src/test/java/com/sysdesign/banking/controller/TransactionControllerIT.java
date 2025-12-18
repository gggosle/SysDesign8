package com.sysdesign.banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysdesign.banking.config.WebConfig;
import com.sysdesign.banking.dto.TransferRequest;
import com.sysdesign.banking.dto.PaymentRequest;
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
    }

    @Test
    void transferEndpointReturnsTransaction() throws Exception {
        TransferRequest req = TransferRequest.builder()
                .fromAccountId(2L)
                .toAccountId(3L)
                .amount(new BigDecimal("50.00"))
                .description("desc")
                .userId(42)
                .build();

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void paymentEndpointReturnsTransaction() throws Exception {
        PaymentRequest req = PaymentRequest.builder()
                .fromAccountId(2L)
                .recipientAccount("5005")
                .amount(new BigDecimal("25.50"))
                .description("pay")
                .userId(42)
                .build();

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void auditEndpointReturnsList() throws Exception {
        mockMvc.perform(get("/api/audit/218242"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].action").value("TRANSFER"));
    }
}