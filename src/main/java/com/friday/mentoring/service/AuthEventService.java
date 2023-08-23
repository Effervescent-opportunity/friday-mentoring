package com.friday.mentoring.service;

import com.friday.mentoring.db.entity.AuthEventEntity;
import com.friday.mentoring.db.entity.OutboxEntity;
import com.friday.mentoring.db.repository.AuthEventRepository;
import com.friday.mentoring.db.repository.OutboxRepository;
import com.friday.mentoring.dto.AuthEventDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * gets auth event, puts it into auth event and outbox table in one transaction
 */
@Component
public class AuthEventService {

    private final AuthEventRepository authEventRepository;
    private final OutboxRepository outboxRepository;

    public AuthEventService(AuthEventRepository authEventRepository, OutboxRepository outboxRepository) {
        this.authEventRepository = authEventRepository;
        this.outboxRepository = outboxRepository;
    }

    @Transactional
    public void saveEvent(AuthEventDto authEventDto) {


        AuthEventEntity authEventEntity = new AuthEventEntity();
        OutboxEntity outboxEntity = new OutboxEntity();
        authEventRepository.saveAndFlush(authEventEntity);
        outboxRepository.saveAndFlush(outboxEntity);
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
