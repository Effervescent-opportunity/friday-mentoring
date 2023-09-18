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
 * Обрабатывает события аудита (сохраняет в базу, отправляет в Кафку)
 */
@Component
public class AuthEventService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEventService.class);

    private final AuthEventRepository authEventRepository;
    private final OutboxRepository outboxRepository;
    private final KafkaProducer kafkaProducer;

    public AuthEventService(AuthEventRepository authEventRepository, OutboxRepository outboxRepository, KafkaProducer kafkaProducer) {
        this.authEventRepository = authEventRepository;
        this.outboxRepository = outboxRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional
    public void processEvent(AuthEventDto authEventDto) {
        AuthEventEntity authEventEntity = new AuthEventEntity(authEventDto);
        OutboxEntity outboxEntity = new OutboxEntity(authEventDto);

        authEventRepository.save(authEventEntity);
        outboxRepository.save(outboxEntity);

        if (kafkaProducer.sendAuthEvent(authEventDto)) {
            outboxRepository.deleteById(outboxEntity.getId());
        } else {
            outboxEntity.setRetryCount(outboxEntity.getRetryCount() - 1);
            outboxRepository.save(outboxEntity);
        }
    }

}
