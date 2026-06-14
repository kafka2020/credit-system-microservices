package ru.netology.credit.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.credit.api.dto.CreditApplicationDto;
import ru.netology.credit.api.model.ApplicationStatus;
import ru.netology.credit.api.service.CreditApiService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/credit")
@RequiredArgsConstructor
public class CreditController {

    private final CreditApiService creditApiService;

    @PostMapping("/apply")
    public ResponseEntity<Map<String, Long>> applyForCredit(@RequestBody CreditApplicationDto dto) {
        log.info("Received credit application request: amount={}, term={}", dto.getAmount(), dto.getTermMonths());
        Long id = creditApiService.submitApplication(dto);
        return ResponseEntity.ok(Map.of("applicationId", id));
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<Map<String, String>> getStatus(@PathVariable Long id) {
        ApplicationStatus status = creditApiService.getStatus(id);
        return ResponseEntity.ok(Map.of(
                "applicationId", String.valueOf(id),
                "status", status.name()
        ));
    }
}
