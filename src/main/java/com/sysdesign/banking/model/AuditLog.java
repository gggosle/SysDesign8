package com.sysdesign.banking.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "audit_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    private String action;

    @Column(name = "user_id")
    private Integer userId;

    // Postgres INET type → String
    @Column(name = "ip_address", columnDefinition = "INET")
    private String ipAddress;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode details;

    private LocalDateTime timestamp = LocalDateTime.now();
}
