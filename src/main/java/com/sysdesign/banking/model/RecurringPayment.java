package com.sysdesign.banking.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "recurring_payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecurringPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "recipient_account", length = 50)
    private String recipientAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", columnDefinition = "frequency_enum")
    private Frequency frequency;

    @Column(name = "next_payment_date")
    private LocalDate nextPaymentDate;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
