package ru.netology.credit.processor.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.netology.credit.processor.event.CreditDecisionEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditDecisionProducer {

    @Value("${rabbitmq.queue.decision}")
    private String decisionQueue;

    private final RabbitTemplate rabbitTemplate;

    public void sendDecision(CreditDecisionEvent event) {
        log.info("Sending credit decision to RabbitMQ: applicationId={}, approved={}",
                event.getApplicationId(), event.isApproved());
        rabbitTemplate.convertAndSend(decisionQueue, event);
    }
}
