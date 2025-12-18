package com.sysdesign.banking.repo;

import com.sysdesign.banking.model.Transaction;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByFromAccountIdOrToAccountId(Long from, Long to, Pageable pageable);

    List<Transaction> findByFromAccountIdOrderByCreatedAtDesc(Long fromAccountId, Pageable pageable);

    List<Transaction> findByToAccountIdOrderByCreatedAtDesc(Long toAccountId, Pageable pageable);

    // New: date-range queries used for statements
    List<Transaction> findByFromAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long fromAccountId, LocalDateTime start, LocalDateTime end);

    List<Transaction> findByToAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long toAccountId, LocalDateTime start, LocalDateTime end);

    // Helper for analytics: fetch outgoing transactions for multiple account ids
    List<Transaction> findByFromAccountIdIn(java.util.Collection<Long> accountIds);
}
