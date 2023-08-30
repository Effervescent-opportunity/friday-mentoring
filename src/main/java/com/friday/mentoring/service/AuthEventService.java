package com.friday.mentoring.service;

import com.friday.mentoring.db.entity.AuthEventEntity;
import com.friday.mentoring.db.entity.OutboxEntity;
import com.friday.mentoring.db.repository.AuthEventRepository;
import com.friday.mentoring.db.repository.OutboxRepository;
import com.friday.mentoring.dto.AuthEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * gets auth event, puts it into auth event and outbox table in one transaction
 */
@Component
public class AuthEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEventService.class);

    private final AuthEventRepository authEventRepository;
    private final OutboxRepository outboxRepository;

    private final TransactionTemplate transactionTemplate;
    private final KafkaProducer kafkaProducer;

    public AuthEventService(AuthEventRepository authEventRepository, OutboxRepository outboxRepository,
                            TransactionTemplate transactionTemplate, KafkaProducer kafkaProducer) {
        this.authEventRepository = authEventRepository;
        this.outboxRepository = outboxRepository;
        this.transactionTemplate = transactionTemplate;
        this.kafkaProducer = kafkaProducer;
    }

    public void processEvent(AuthEventDto authEventDto) {
        AuthEventEntity authEventEntity = new AuthEventEntity(authEventDto);
        OutboxEntity outboxEntity = new OutboxEntity(authEventDto);

        LOGGER.info("LALALA Has authEventEntity [{}] and outboxEntity [{}]", authEventEntity, outboxEntity);

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            authEventRepository.save(authEventEntity);
            outboxRepository.save(outboxEntity);
        });

        LOGGER.info("LALALA Has authEventEntity [{}] and outboxEntity [{}] after saving",
                authEventEntity, outboxEntity);

//todo send to kafka here and delete from outbox OR call outbox retry service

        boolean wasSent = kafkaProducer.sendAuthEvent(authEventDto);
        if (wasSent) {
            LOGGER.info("LALALA outbox [{}] was sent", outboxEntity);
                outboxRepository.delete(outboxEntity);
        } else {
            LOGGER.info("LALALA outbox [{}] was not sent", outboxEntity);
        }
    }

    //todo this here:

    //    @Override
    //    public void create(int id, String description) {
    //        UUID outboxId = UUID.randomUUID();
    //        String message = buildMessage(id, description);
    //
    //        transactionTemplate.executeWithoutResult(transactionStatus -> {
    //            orderRepository.save(id, description);
    //            outboxRepository.save(new OutboxEntity(outboxId, message));
    //        });
    //
    //        deliveryMessageQueueService.send(message);
    //
    //        // it is better to execute this line asynchonically
    //        outboxRepository.delete(outboxId);
    //    }
    //
    //    private String buildMessage(int id, String description) {
    //        // ...
    //    }

}
