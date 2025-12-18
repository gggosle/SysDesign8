package com.sysdesign.banking.service;

import com.sysdesign.banking.dto.RecurringSetupRequest;
import com.sysdesign.banking.model.RecurringPayment;
import com.sysdesign.banking.model.Frequency;
import com.sysdesign.banking.repo.RecurringPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

/**
 * Сервіс для роботи з повторюваними платежами (recurring payments).
 *
 * Опис:
 * - Надає методи для створення та управління налаштуваннями повторюваних платежів.
 * - Зберігає об'єкт {@link RecurringPayment} у відповідному репозиторії.
 */
@Service
@RequiredArgsConstructor
public class RecurringService {

    private final RecurringPaymentRepository recurringRepo;

    /**
     * Налаштувати (створити) повторюваний платіж.
     *
     * Вхідні параметри:
     * @param req DTO {@link RecurringSetupRequest} який містить accountId, amount, frequency, nextPaymentDate, recipientAccount
     *
     * Повертає:
     * - Збережений об'єкт {@link RecurringPayment}.
     *
     * Примітки:
     * - Якщо nextPaymentDate не вказано, використовується сьогоднішня дата.
     * - Поле frequency парситься через {@link Frequency#valueOf(String)}; викликач повинен передавати коректне значення.
     */
    @Transactional
    public RecurringPayment setup(RecurringSetupRequest req) {
        RecurringPayment rp = RecurringPayment.builder()
                .id(req.getAccountId())
                .amount(req.getAmount())
                .recipientAccount(req.getRecipientAccount())
                .frequency(Frequency.valueOf(req.getFrequency()))
                .nextPaymentDate(req.getNextPaymentDate() != null ? req.getNextPaymentDate() : LocalDate.now())
                .isActive(true)
                .build();
        return recurringRepo.save(rp);
    }
}
