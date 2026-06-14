package ru.netology.credit.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.credit.api.dto.CreditApplicationDto;
import ru.netology.credit.api.event.CreditApplicationEvent;
import ru.netology.credit.api.event.CreditDecisionEvent;
import ru.netology.credit.api.kafka.CreditApplicationProducer;
import ru.netology.credit.api.model.ApplicationStatus;
import ru.netology.credit.api.model.CreditApplication;
import ru.netology.credit.api.repository.CreditApplicationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditApiService {

    private final CreditApplicationRepository repository;
    private final CreditApplicationProducer kafkaProducer;

    @Transactional
    public Long submitApplication(CreditApplicationDto dto) {
        CreditApplication application = CreditApplication.builder()
                .amount(dto.getAmount())
                .termMonths(dto.getTermMonths())
                .monthlyIncome(dto.getMonthlyIncome())
                .currentDebtLoad(dto.getCurrentDebtLoad())
                .creditScore(dto.getCreditScore())
                .status(ApplicationStatus.PROCESSING)
                .build();

        CreditApplication saved = repository.save(application);
        log.info("Credit application saved with id={}", saved.getId());

        CreditApplicationEvent event = CreditApplicationEvent.builder()
                .applicationId(saved.getId())
                .amount(saved.getAmount())
                .termMonths(saved.getTermMonths())
                .monthlyIncome(saved.getMonthlyIncome())
                .currentDebtLoad(saved.getCurrentDebtLoad())
                .creditScore(saved.getCreditScore())
                .build();

        kafkaProducer.sendApplication(event);
        return saved.getId();
    }

    public ApplicationStatus getStatus(Long id) {
        return repository.findById(id)
                .map(CreditApplication::getStatus)
                .orElseThrow(() -> new RuntimeException("Application not found: " + id));
    }

    @Transactional
    public void updateApplicationStatus(CreditDecisionEvent event) {
        repository.findById(event.getApplicationId()).ifPresent(app -> {
            app.setStatus(event.isApproved() ? ApplicationStatus.APPROVED : ApplicationStatus.REJECTED);
            repository.save(app);
            log.info("Updated application id={} status to {}", app.getId(), app.getStatus());
        });
    }
}
