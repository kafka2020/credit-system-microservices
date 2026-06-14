package ru.netology.credit.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.netology.credit.api.model.CreditApplication;

public interface CreditApplicationRepository extends JpaRepository<CreditApplication, Long> {
}
