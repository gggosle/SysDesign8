package com.sysdesign.banking.service;

import com.sysdesign.banking.repo.TransactionRepository;
import com.sysdesign.banking.dto.SpendingAnalyticsResponse;
import com.sysdesign.banking.repo.AccountRepository;
import com.sysdesign.banking.model.Transaction;
import com.sysdesign.banking.model.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public SpendingAnalyticsResponse spending(Integer userId) {
        // fetch user's accounts
        var accounts = accountRepository.findByUserId(userId);
        if (accounts == null || accounts.isEmpty()) {
            return SpendingAnalyticsResponse.builder()
                    .totalSpent(BigDecimal.ZERO)
                    .byType(Collections.emptyMap())
                    .build();
        }

        List<Long> accountIds = accounts.stream().map(a -> a.getId()).collect(Collectors.toList());

        // fetch outgoing transactions for these accounts
        List<Transaction> outgoing = transactionRepository.findByFromAccountIdIn(accountIds);
        if (outgoing == null) outgoing = Collections.emptyList();

        BigDecimal total = BigDecimal.ZERO;
        Map<String, BigDecimal> byType = new HashMap<>();

        for (Transaction t : outgoing) {
            if (t.getAmount() == null) continue;
            // consider deposits as incoming; exclude them from spending
            if (t.getType() == TransactionType.deposit) continue;

            BigDecimal amount = t.getAmount();
            total = total.add(amount);

            String key = t.getType() == null ? "UNKNOWN" : t.getType().name();
            byType.put(key, byType.getOrDefault(key, BigDecimal.ZERO).add(amount));
        }

        return SpendingAnalyticsResponse.builder()
                .totalSpent(total)
                .byType(byType)
                .build();
    }
}
