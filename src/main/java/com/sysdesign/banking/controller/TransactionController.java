package com.sysdesign.banking.controller;

import com.sysdesign.banking.dto.*;
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

    @PostMapping("/transfers")
    public ResponseEntity<Transaction> transfer(@RequestBody TransferRequest req) {
        Transaction tx = transactionService.transfer(req);
        return ResponseEntity.ok(tx);
    }

    @PostMapping("/payments")
    public ResponseEntity<Transaction> payment(@RequestBody PaymentRequest req) {
        Transaction tx = transactionService.payment(req);
        return ResponseEntity.ok(tx);
    }

    @GetMapping("/audit/{transaction_id}")
    public ResponseEntity<List<?>> audit(@PathVariable("transaction_id") Long txId) {
        return ResponseEntity.ok(transactionService.getAuditTrail(txId));
    }
}
