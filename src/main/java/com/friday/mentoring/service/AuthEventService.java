package com.friday.mentoring.service;

import com.friday.mentoring.db.entity.AuthEventEntity;
import com.friday.mentoring.db.entity.OutboxEntity;
import com.friday.mentoring.db.repository.AuthEventRepository;
import com.friday.mentoring.db.repository.OutboxRepository;
import com.friday.mentoring.dto.AuthEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * gets auth event, puts it into auth event and outbox table in one transaction
 */
@Component
public class AuthEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEventService.class);

    private final AuthEventRepository authEventRepository;
    private final OutboxRepository outboxRepository;

    public AuthEventService(AuthEventRepository authEventRepository, OutboxRepository outboxRepository) {
        this.authEventRepository = authEventRepository;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public void saveEvent(AuthEventDto authEventDto) {

        AuthEventEntity authEventEntity = new AuthEventEntity(authEventDto);
        OutboxEntity outboxEntity = new OutboxEntity(authEventDto);

        LOGGER.info("LALALA Has authEventEntity [{}] and outboxEntity [{}]", authEventEntity, outboxEntity);

//todo do in separate transaction
        //todo read about the difference between save and saveAndFlush()
        AuthEventEntity authEventEntity1 = authEventRepository.saveAndFlush(authEventEntity);
        OutboxEntity outboxEntity1 = outboxRepository.saveAndFlush(outboxEntity);

        LOGGER.info("LALALA Has authEventEntity [{}] and outboxEntity [{}] after saving: authEventEntity1 [{}] outboxEntity1 [{}]",
                authEventEntity, outboxEntity, authEventEntity1, outboxEntity1);

//todo send to kafka here and delete from outbox OR call outbox retry service


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
