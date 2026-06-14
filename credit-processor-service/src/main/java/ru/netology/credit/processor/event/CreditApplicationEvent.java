package ru.netology.credit.processor.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditApplicationEvent {
    private Long applicationId;
    private BigDecimal amount;
    private Integer termMonths;
    private BigDecimal monthlyIncome;
    private BigDecimal currentDebtLoad;
    private Integer creditScore;
}
