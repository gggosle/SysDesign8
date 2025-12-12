package com.sysdesign.banking.service;

import com.sysdesign.banking.dto.RecurringSetupRequest;
import com.sysdesign.banking.model.RecurringPayment;
import com.sysdesign.banking.model.Frequency;
import com.sysdesign.banking.repo.RecurringPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RecurringService {

    private final RecurringPaymentRepository recurringRepo;

    @Transactional
    public RecurringPayment setup(RecurringSetupRequest req) {
        RecurringPayment rp = RecurringPayment.builder()
                .id(req.getAccountId())
                .amount(req.getAmount())
                .recipientAccount(req.getRecipientAccount())
                .frequency(Frequency.valueOf(req.getFrequency().toUpperCase()))
                .nextPaymentDate(req.getNextPaymentDate() != null ? req.getNextPaymentDate() : LocalDate.now())
                .isActive(true)
                .build();
        return recurringRepo.save(rp);
    }
}
