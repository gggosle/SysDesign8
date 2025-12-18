package com.sysdesign.banking.dto;
import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatementResponse {
    private String month; // YYYY-MM
    private List<TransactionResponse> transactions; // use TransactionResponse DTO
}