package com.sysdesign.banking.repo;

import com.sysdesign.banking.model.RecurringPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDate;

@Repository
public interface RecurringPaymentRepository extends JpaRepository<RecurringPayment, Long> {
    List<RecurringPayment> findByNextPaymentDateBeforeAndIsActive(LocalDate date, Boolean isActive);
}