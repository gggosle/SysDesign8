package com.sysdesign.banking.repo;

import com.sysdesign.banking.model.Transaction;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByFromAccountIdOrToAccountId(Long from, Long to, Pageable pageable);

    List<Transaction> findByFromAccountIdOrderByCreatedAtDesc(Long fromAccountId, Pageable pageable);

    List<Transaction> findByToAccountIdOrderByCreatedAtDesc(Long toAccountId, Pageable pageable);
}
