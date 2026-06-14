package ru.netology.credit.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditApplicationDto {
    private BigDecimal amount;
    private Integer termMonths;
    private BigDecimal monthlyIncome;
    private BigDecimal currentDebtLoad;
    private Integer creditScore;
}
