package ru.netology.credit.processor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.credit.processor.event.CreditApplicationEvent;
import ru.netology.credit.processor.event.CreditDecisionEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
public class CreditDecisionService {

    private static final BigDecimal MAX_DEBT_INCOME_RATIO = new BigDecimal("0.50");
    private static final int ANNUAL_RATE_PERCENT = 15;

    public CreditDecisionEvent evaluate(CreditApplicationEvent event) {
        BigDecimal monthlyPayment = calculateMonthlyPayment(
                event.getAmount(), event.getTermMonths());

        BigDecimal totalMonthlyDebt = monthlyPayment.add(event.getCurrentDebtLoad());
        BigDecimal maxAllowedDebt = event.getMonthlyIncome().multiply(MAX_DEBT_INCOME_RATIO);

        log.info("Evaluating application id={}: monthlyPayment={}, totalDebt={}, maxAllowed={}, income={}",
                event.getApplicationId(), monthlyPayment, totalMonthlyDebt,
                maxAllowedDebt, event.getMonthlyIncome());

        boolean approved = totalMonthlyDebt.compareTo(maxAllowedDebt) <= 0;
        String reason = approved
                ? String.format("Approved. Monthly payment: %.2f. Total debt load: %.2f (%.1f%% of income)",
                    monthlyPayment, totalMonthlyDebt,
                    totalMonthlyDebt.divide(event.getMonthlyIncome(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)))
                : String.format("Rejected. Total monthly obligations %.2f exceed 50%% of income %.2f",
                    totalMonthlyDebt, event.getMonthlyIncome());

        return CreditDecisionEvent.builder()
                .applicationId(event.getApplicationId())
                .approved(approved)
                .reason(reason)
                .build();
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, int termMonths) {
        double monthlyRate = ANNUAL_RATE_PERCENT / 100.0 / 12;
        double factor = Math.pow(1 + monthlyRate, termMonths);
        double payment = amount.doubleValue() * (monthlyRate * factor) / (factor - 1);
        return BigDecimal.valueOf(payment).setScale(2, RoundingMode.HALF_UP);
    }
}
