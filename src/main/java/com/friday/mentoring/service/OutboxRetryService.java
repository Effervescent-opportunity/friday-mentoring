package com.friday.mentoring.service;

import com.friday.mentoring.db.entity.OutboxEntity;
import com.friday.mentoring.db.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

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
        List<OutboxEntity> outboxEntities = outboxRepository.findTop10ByCreatedAtBetween(OffsetDateTime.ofInstant(Instant.now().minus(1, ChronoUnit.DAYS), ZoneId.systemDefault()),
                OffsetDateTime.ofInstant(Instant.now().minusSeconds(2), ZoneId.systemDefault()));



        for (OutboxEntity outbox : outboxEntities) {
            //outbox.createdAt with offset Z (UTC)
            //outbox.getEvent.time() with offset 3 (Moscow)

            //2023-08-27T22:14:26.274+03:00  INFO 10519 --- [0.1-8443-exec-5] c.f.mentoring.service.AuthEventService   : LALALA Has authEventEntity [AuthEventEntity{id=fcb4f986-ccaa-4de8-8770-069729edef18, ipAddress='127.0.0.1', eventTime=2023-08-27T22:14:26.180573141+03:00, userName='anonymousUser', type='AUTHORIZATION_FAILURE'}] and outboxEntity [OutboxEntity{id=8bc9dda3-d794-403b-a5ee-ca1a8cf6fe6b, createdAt=2023-08-27T22:14:26.187450224+03:00, retryCount=5, event=AuthEventDto[ipAddress=127.0.0.1, time=2023-08-27T22:14:26.180573141+03:00, userName=anonymousUser, type=AUTHORIZATION_FAILURE]}] after saving: authEventEntity1 [AuthEventEntity{id=fcb4f986-ccaa-4de8-8770-069729edef18, ipAddress='127.0.0.1', eventTime=2023-08-27T22:14:26.180573141+03:00, userName='anonymousUser', type='AUTHORIZATION_FAILURE'}] outboxEntity1 [OutboxEntity{id=8bc9dda3-d794-403b-a5ee-ca1a8cf6fe6b, createdAt=2023-08-27T22:14:26.187450224+03:00, retryCount=5, event=AuthEventDto[ipAddress=127.0.0.1, time=2023-08-27T22:14:26.180573141+03:00, userName=anonymousUser, type=AUTHORIZATION_FAILURE]}]
            //2023-08-27T22:14:30.012+03:00  INFO 10519 --- [   scheduling-1] c.f.m.service.OutboxRetryService         : LALALA outbox [OutboxEntity{id=8bc9dda3-d794-403b-a5ee-ca1a8cf6fe6b, createdAt=2023-08-27T19:14:26.187450Z, retryCount=5, event=AuthEventDto[ipAddress=127.0.0.1, time=2023-08-27T22:14:26.180573141+03:00, userName=anonymousUser, type=AUTHORIZATION_FAILURE]}] was sent
            //todo turn on debug and see what converter is used - my custom Json serializer or what?
            boolean wasSent = kafkaProducer.sendAuthEvent(outbox.getEvent());
            if (wasSent) {
                LOGGER.info("LALALA outbox [{}] was sent", outbox);
//                outboxRepository.delete(outbox);
            }
        }
    }

//todo how kafka checks uniqueness& I have same messages in Kafka UI
}
