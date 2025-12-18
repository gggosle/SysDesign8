package com.sysdesign.banking.controller;

import com.sysdesign.banking.dto.*;
import com.sysdesign.banking.dto.mapper.AuditLogMapper;
import com.sysdesign.banking.dto.mapper.TransactionMapper;
import com.sysdesign.banking.model.Transaction;
import com.sysdesign.banking.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final AuditLogMapper auditLogMapper;

    @PostMapping("/transfers")
    public ResponseEntity<TransactionResponse> transfer(@RequestBody TransferRequest req) {
        Transaction tx = transactionService.transfer(req);
        return ResponseEntity.ok(transactionMapper.toDto(tx));
    }

    @PostMapping("/payments")
    public ResponseEntity<TransactionResponse> payment(@RequestBody PaymentRequest req) {
        Transaction tx = transactionService.payment(req);
        return ResponseEntity.ok(transactionMapper.toDto(tx));
    }

    @GetMapping("/audit/{transaction_id}")
    public ResponseEntity<List<AuditLogResponse>> audit(@PathVariable("transaction_id") Long txId) {
        List<AuditLogResponse> auditLogResponses = transactionService.getAuditTrail(txId)
                .stream()
                .map(auditLogMapper::toDto)
                .toList();

        return ResponseEntity.ok(auditLogResponses);
    }
}
