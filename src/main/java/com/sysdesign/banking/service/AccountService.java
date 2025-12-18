package com.sysdesign.banking.service;

import com.sysdesign.banking.dto.AccountResponse;
import com.sysdesign.banking.dto.mapper.AccountMapper;
import com.sysdesign.banking.model.Account;
import com.sysdesign.banking.repo.AccountRepository;
import com.sysdesign.banking.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервіс для роботи з банківськими рахунками (Account).
 *
 * Опис:
 * - Забезпечує операції читання і модифікації даних рахунку через {@link AccountRepository}.
 * - Використовує {@link AccountMapper} для перетворення між сутністю та DTO (якщо потрібно).
 * - Критичні операції оновлення рахунку виконує в транзакціях з відповідним блокуванням,
 *   щоб уникнути умов гонки при конкурентних переказах/змінах балансу.
 *
 * Зауваження про винятки:
 * - Якщо ресурс не знайдено, методи кидають {@link NotFoundException}.
 * - Метод `findAndLock` використовує PESSIMISTIC_WRITE lock, тому може блокуватися до
 *   завершення транзакції; викликачі повинні бути готові обробити довші затримки.
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    /**
     * Повертає список об'єктів {@link Account} для вказаного користувача.
     *
     * Вхідні параметри:
     * @param userId ідентифікатор користувача (зовнішній, як правило Integer).
     *
     * Повертає:
     * - список сутностей {@link Account}; порожній список, якщо рахунків не знайдено.
     *
     * Примітка:
     * - Для REST API зазвичай краще повертати DTO (наприклад, {@link AccountResponse}).
     *   Цей метод повертає сутності для внутрішнього використання; якщо потрібно – можна
     *   додати окремий метод, який мапить на DTO за допомогою {@link AccountMapper}.
     */
    public List<Account> listByUser(Integer userId) {
        return accountRepository.findByUserId(userId);
    }

    /**
     * Повертає баланс рахунку за його ідентифікатором.
     *
     * Вхідні параметри:
     * @param accountId ідентифікатор рахунку
     *
     * Повертає:
     * - {@link BigDecimal} баланс рахунку.
     *
     * Викидає:
     * - {@link NotFoundException} якщо рахунок не знайдено.
     */
    public BigDecimal getBalance(Long accountId) {
        return accountRepository.findById(accountId)
                .map(Account::getBalance)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }

    /**
     * Заблокувати або розблокувати рахунок у межах транзакції.
     *
     * Опис:
     * - Метод атомарно отримує запис рахунку за допомогою спеціального запиту
     *   з PESSIMISTIC_WRITE блокуванням (реалізовано у {@link AccountRepository#findByIdForUpdate}).
     * - Після встановлення прапора блокування змінений рахунок зберігається.
     *
     * Вхідні параметри:
     * @param accountId ідентифікатор рахунку
     * @param lock true — заблокувати, false — розблокувати
     *
     * Повертає:
     * - Збережену сутність {@link Account} після оновлення.
     *
     * Викидає:
     * - {@link NotFoundException} якщо рахунок не знайдено.
     */
    @Transactional
    public Account lockOrUnlock(Long accountId, boolean lock) {
        Account acct = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        acct.setIsLocked(lock);
        return accountRepository.save(acct);
    }

    /**
     * Знайти рахунок та отримати блокування для подальших змін (в середині транзакції).
     *
     * Деталі:
     * - Використовується для операцій, які потребують гарантій ACID (наприклад, переведення коштів).
     * - Повертає сутність {@link Account} вже завантажену з PESSIMISTIC_WRITE блокуванням.
     *
     * Вхідні параметри:
     * @param accountId ідентифікатор рахунку
     *
     * Повертає:
     * - {@link Account} (якщо знайдено).
     *
     * Викидає:
     * - {@link NotFoundException} якщо рахунок не знайдено.
     */
    @Transactional
    public Account findAndLock(Long accountId) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }

    /**
     * Зберігає сутність {@link Account} у базі даних.
     *
     * Примітка:
     * - Цей метод обгорнутий у транзакцію і підходить для простого збереження/оновлення;
     *   складні бізнес-правила краще реалізовувати у вищих шарах сервісу.
     */
    @Transactional
    public void save(Account a) {
        accountRepository.save(a);
    }
}
