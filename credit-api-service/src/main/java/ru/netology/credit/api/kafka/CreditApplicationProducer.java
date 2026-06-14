package ru.netology.credit.api.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.netology.credit.api.event.CreditApplicationEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditApplicationProducer {

    private static final String TOPIC = "credit-applications";

    private final KafkaTemplate<String, CreditApplicationEvent> kafkaTemplate;

    public void sendApplication(CreditApplicationEvent event) {
        log.info("Sending credit application event to Kafka: applicationId={}", event.getApplicationId());
        kafkaTemplate.send(TOPIC, String.valueOf(event.getApplicationId()), event);
    }
}
