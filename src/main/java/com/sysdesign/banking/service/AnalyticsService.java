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

/**
 * Сервіс для аналітики витрат користувача.
 *
 * Опис:
 * - Обчислює сумарні витрати користувача та розподіл витрат за типами транзакцій.
 * - Витрати інтерпретуються як вихідні транзакції (тобто транзакції, де користувач
 *   є відправником — `fromAccount`). Депозити (вхідні) за замовчуванням не включені.
 *
 * Зауваження щодо продуктивності:
 * - Використовується метод репозиторію `findByFromAccountIdIn(...)`, який формує SQL
 *   з `IN (...)`. Для великої кількості ідентифікаторів це може бути неефективно;
 *   у такому випадку розгляньте альтернативні підходи (запит з JOIN або батчинг).
 */
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    /**
     * Обчислити витрати користувача.
     *
     * Вхідні параметри:
     * @param userId ідентифікатор користувача
     *
     * Повертає:
     * - {@link SpendingAnalyticsResponse} з полем totalSpent (сума витрат) та
     *   byType — мапа, що містить суму витрат по кожному типу транзакції.
     *
     * Поведінка:
     * - Якщо у користувача немає рахунків, повертається нульовий результат.
     * - Транзакції з null сумою пропускаються; депозити (тип `deposit`) не враховуються.
     */
    public SpendingAnalyticsResponse spending(Integer userId) {
        // отримати рахунки користувача
        var accounts = accountRepository.findByUserId(userId);
        if (accounts == null || accounts.isEmpty()) {
            return SpendingAnalyticsResponse.builder()
                    .totalSpent(BigDecimal.ZERO)
                    .byType(Collections.emptyMap())
                    .build();
        }

        List<Long> accountIds = accounts.stream().map(a -> a.getId()).collect(Collectors.toList());

        // отримати вихідні транзакції для цих рахунків
        List<Transaction> outgoing = transactionRepository.findByFromAccountIdIn(accountIds);
        if (outgoing == null) outgoing = Collections.emptyList();

        BigDecimal total = BigDecimal.ZERO;
        Map<String, BigDecimal> byType = new HashMap<>();

        for (Transaction t : outgoing) {
            if (t.getAmount() == null) continue;
            // депозит — це вхідна операція, не вважаємо її витратою
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
