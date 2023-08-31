package com.friday.mentoring.service;

import com.friday.mentoring.db.entity.OutboxEntity;
import com.friday.mentoring.db.repository.OutboxRepository;
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
public class OutboxRetryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxRetryService.class);

    private final KafkaProducer kafkaProducer;
    private final OutboxRepository outboxRepository;

    public OutboxRetryService(KafkaProducer kafkaProducer, OutboxRepository outboxRepository) {
        this.kafkaProducer = kafkaProducer;
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedDelay = 1L)
    public void retrySendingToKafka() {
        List<OutboxEntity> outboxEntities = outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(0,
                OffsetDateTime.ofInstant(Instant.now().minus(1, ChronoUnit.DAYS), ZoneId.systemDefault()),
                OffsetDateTime.ofInstant(Instant.now().minusSeconds(2), ZoneId.systemDefault()));

        for (OutboxEntity outbox : outboxEntities) {
            boolean wasSent = kafkaProducer.sendAuthEvent(outbox.getEvent());
            if (wasSent) {
                LOGGER.debug("OutboxEntity [{}] was sent to Kafka, it will be deleted", outbox);
                outboxRepository.deleteById(outbox.getId());
            } else {
                LOGGER.info("OutboxEntity [{}] was not sent to Kafka, it's retry count will be decremented", outbox);
                outbox.setRetryCount(outbox.getRetryCount() - 1);
                outboxRepository.save(outbox);
            }
        }
    }

}
