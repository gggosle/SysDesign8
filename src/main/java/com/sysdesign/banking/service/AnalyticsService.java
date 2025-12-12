package com.sysdesign.banking.service;

import com.sysdesign.banking.repo.TransactionRepository;
import com.sysdesign.banking.dto.SpendingAnalyticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;


@Service
@RequiredArgsConstructor
public class AnalyticsService {

    public SpendingAnalyticsResponse spending(Integer userId) {
        BigDecimal total = BigDecimal.ZERO;
        Map<String, BigDecimal> byType = new HashMap<>();

        return SpendingAnalyticsResponse.builder()
                .totalSpent(total)
                .byType(byType)
                .build();
    }
    // TODO: implement analytics
}
