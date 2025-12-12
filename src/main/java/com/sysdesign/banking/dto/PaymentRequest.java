package com.sysdesign.banking.dto;

import java.math.BigDecimal;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentRequest {
    private Long fromAccountId;
    private String recipientAccount;
    private BigDecimal amount;
    private String description;
    private Integer userId;
}