package com.sysdesign.banking.controller;

import com.sysdesign.banking.dto.*;
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

    @PostMapping("/transfers")
    public ResponseEntity<TransactionResponse> transfer(@RequestBody TransferRequest req) {
        TransactionResponse tx = transactionService.transfer(req);
        return ResponseEntity.ok(tx);
    }

    @PostMapping("/payments")
    public ResponseEntity<TransactionResponse> payment(@RequestBody PaymentRequest req) {
        TransactionResponse tx = transactionService.payment(req);
        return ResponseEntity.ok(tx);
    }

    @GetMapping("/audit/{transaction_id}")
    public ResponseEntity<List<AuditLogResponse>> audit(@PathVariable("transaction_id") Long txId) {
        return ResponseEntity.ok(transactionService.getAuditTrail(txId));
    }
}
