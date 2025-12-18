package com.sysdesign.banking.dto;
import lombok.*;
import java.util.List;

/**
 * DTO для відповіді місячної виписки.
 * month — рядок у форматі YYYY-MM
 * transactions — список транзакцій за вказаний місяць
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatementResponse {
    private String month; // YYYY-MM
    private List<TransactionResponse> transactions; // use TransactionResponse DTO
}