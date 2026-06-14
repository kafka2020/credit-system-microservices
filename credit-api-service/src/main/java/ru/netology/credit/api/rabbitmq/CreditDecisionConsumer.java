package ru.netology.credit.api.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.netology.credit.api.event.CreditDecisionEvent;
import ru.netology.credit.api.service.CreditApiService;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditDecisionConsumer {

    private final CreditApiService creditApiService;

    @RabbitListener(queues = "${rabbitmq.queue.decision}")
    public void receiveDecision(CreditDecisionEvent event) {
        log.info("Received credit decision from RabbitMQ: applicationId={}, approved={}",
                event.getApplicationId(), event.isApproved());
        creditApiService.updateApplicationStatus(event);
    }
}
