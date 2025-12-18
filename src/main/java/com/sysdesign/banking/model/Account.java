package com.sysdesign.banking.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "account_number", unique = true, nullable = false, length = 20)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "account_type", columnDefinition = "account_type_enum")
    private AccountType accountType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(length = 3)
    private String currency = "USD";

    @Column(name = "is_locked")
    private Boolean isLocked = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "fromAccount")
    private List<Transaction> outgoingTransactions;

    @OneToMany(mappedBy = "toAccount")
    private List<Transaction> incomingTransactions;

    @OneToMany(mappedBy = "account")
    private List<RecurringPayment> recurringPayments;
}
