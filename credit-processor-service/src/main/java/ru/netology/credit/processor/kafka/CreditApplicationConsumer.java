package ru.netology.credit.processor.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.netology.credit.processor.event.CreditApplicationEvent;
import ru.netology.credit.processor.event.CreditDecisionEvent;
import ru.netology.credit.processor.rabbitmq.CreditDecisionProducer;
import ru.netology.credit.processor.service.CreditDecisionService;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditApplicationConsumer {

    private final CreditDecisionService decisionService;
    private final CreditDecisionProducer decisionProducer;

    @KafkaListener(
            topics = "credit-applications",
            groupId = "credit-processor-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void processApplication(CreditApplicationEvent event) {
        log.info("Received credit application from Kafka: applicationId={}", event.getApplicationId());
        CreditDecisionEvent decision = decisionService.evaluate(event);
        log.info("Decision for applicationId={}: approved={}, reason={}",
                decision.getApplicationId(), decision.isApproved(), decision.getReason());
        decisionProducer.sendDecision(decision);
    }
}
