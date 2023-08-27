package com.friday.mentoring.service;

import com.friday.mentoring.db.entity.OutboxEntity;
import com.friday.mentoring.db.repository.OutboxRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Resends events to Kafka and deletes sent
 */
public class OutboxRetryService {

    private final KafkaProducer kafkaProducer;
    private final OutboxRepository outboxRepository;

    public OutboxRetryService(KafkaProducer kafkaProducer, OutboxRepository outboxRepository) {
        this.kafkaProducer = kafkaProducer;
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedDelay = 1L)
    public void retrySendingToKafka() {
        List<OutboxEntity> outboxEntities = outboxRepository.findTop10ByCreatedAtBetween(OffsetDateTime.ofInstant(Instant.now().minus(1, ChronoUnit.DAYS), ZoneId.systemDefault()),
                OffsetDateTime.ofInstant(Instant.now().minusSeconds(2), ZoneId.systemDefault()));

        for (OutboxEntity outbox : outboxEntities) {
            boolean wasSent = kafkaProducer.sendAuthEvent(outbox.getEvent());
            if (wasSent) {
                outboxRepository.delete(outbox);
            }
        }
    }


}
