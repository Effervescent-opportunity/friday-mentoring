package com.friday.mentoring.siem.scheduled;

import com.friday.mentoring.jpa.AuthEventEntity;
import com.friday.mentoring.usecase.AuthEventType;
import com.friday.mentoring.usecase.EventRepository;
import com.friday.mentoring.usecase.SiemEventType;
import com.friday.mentoring.usecase.SiemSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

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
        eventRepository.getNotSentEvents().forEach(authEventEntity -> {
            try {
                if (send(authEventEntity)) {
                    eventRepository.setSuccessStatus(authEventEntity.getId());
                }
            } catch (Exception ex) {
                LOGGER.warn("Got exception while sending event [{}] to SIEM", authEventEntity, ex);
            }
        });
    }

    private boolean send(AuthEventEntity event) {
        SiemEventType eventType = switch (AuthEventType.valueOf(event.getEventType())) {
            case AUTHENTICATION_SUCCESS -> SiemEventType.AUTH_SUCCESS;
            case AUTHENTICATION_FAILURE, AUTHORIZATION_FAILURE -> SiemEventType.AUTH_FAILURE;
        };

        return siemSender.send(event.getIpAddress(), event.getEventTime(), event.getUserName(), eventType);
    }

}
