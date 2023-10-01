package com.friday.mentoring.siem.scheduled;

import com.friday.mentoring.todo.AuthEventType;
import com.friday.mentoring.jpa.AuthEventEntity;
import com.friday.mentoring.usecase.EventRepository;
import com.friday.mentoring.todo.SiemEventType;
import com.friday.mentoring.usecase.SiemSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Сервис, читающий события из БД и отправляющий события в SIEM
 */
@Component
public class ScheduledSiemSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledSiemSender.class);

    private final SiemSender siemSender;
    private final EventRepository eventRepository;

    public ScheduledSiemSender(SiemSender siemSender, EventRepository eventRepository) {
        this.siemSender = siemSender;
        this.eventRepository = eventRepository;
    }

    @Transactional
    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedDelayString = "${siem.send.fixed.delay.seconds:60}")
    public void sendToSiem() {
        try (Stream<AuthEventEntity> stream = eventRepository.getNotSentEvents()) {
            stream.forEach(authEventEntity -> {
                if (siemSender.send(authEventEntity.getIpAddress(), authEventEntity.getEventTime(),
                        authEventEntity.getUserName(), authEventEntity.getEventType() == AuthEventType.AUTHENTICATION_SUCCESS
                                ? SiemEventType.AUTH_SUCCESS : SiemEventType.AUTH_FAILURE)) {
                    eventRepository.setSuccessStatus(authEventEntity.getId());
                }
            });
        } catch (Exception ex) {
            LOGGER.warn("Got exception while sending events to SIEM", ex);
        }
    }

}
