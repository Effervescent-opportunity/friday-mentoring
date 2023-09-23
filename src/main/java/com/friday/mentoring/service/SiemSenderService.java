package com.friday.mentoring.service;

import com.friday.mentoring.db.entity.AuthEventEntity;
import com.friday.mentoring.db.entity.OutboxEntity;
import com.friday.mentoring.db.repository.AuthEventRepository;
import com.friday.mentoring.db.repository.OutboxRepository;
import com.friday.mentoring.dto.AuthEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Сервис, переотправляющий события в Кафку и удаляющий отправленные из базы
 */
@Component
public class SiemSenderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiemSenderService.class);

    private final KafkaProducer kafkaProducer;
    private final AuthEventRepository authEventRepository;

    public SiemSenderService(KafkaProducer kafkaProducer, AuthEventRepository authEventRepository) {
        this.kafkaProducer = kafkaProducer;
        this.authEventRepository = authEventRepository;
    }

    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedDelay = 1L)
    public void retrySendingToKafka() {
        List<AuthEventEntity> authEventEntities = authEventRepository.findAllByWasSentFalse();

        for (AuthEventEntity authEventEntity : authEventEntities) {
            if (kafkaProducer.sendAuthEvent(
                    new AuthEventDto(authEventEntity.getIpAddress(), authEventEntity.getEventTime(),
                            authEventEntity.getUserName(), authEventEntity.getEventType()))) {//todo make beautiful
                //entities should know nothing about dtos
                authEventRepository.setSuccessSentStatus(authEventEntity.getId());
                LOGGER.debug("AuthEventEntity [{}] was sent to Kafka", authEventEntity);
            }
        }
    }

}
