package com.friday.mentoring.siem.scheduled;

import com.friday.mentoring.jpa.AuthEventEntity;
import com.friday.mentoring.usecase.AuthEventType;
import com.friday.mentoring.usecase.SiemEventType;
import com.friday.mentoring.usecase.EventRepository;
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
        eventRepository.getNotSentEvents().forEach(authEventEntity -> {
            try {//todo think if I really need this, is therte exception whent type isn't correct?
                if (send(authEventEntity)) {
                    eventRepository.setSuccessStatus(authEventEntity.getId());
                }
            } catch (Exception ex) {
                LOGGER.warn("Got exception while sending events to SIEM", ex);
            }
        });
    }

    private boolean send(AuthEventEntity event) {
//        SiemEventType eventType = AuthEventType.valueOf(event.getEventType()) == AuthEventType.AUTHN_SUCCESS ?
//                SiemEventType.AUTH_SUCCESS : SiemEventType.AUTH_FAILURE;
        return siemSender.send(event.getIpAddress(), event.getEventTime(), event.getUserName(), getType(event));
    }

    private SiemEventType getType(AuthEventEntity event) {
        return switch (AuthEventType.valueOf(event.getEventType())) {
            case AUTHN_SUCCESS -> SiemEventType.AUTH_SUCCESS;
            case AUTHN_FAILURE, AUTHZ_FAILURE -> SiemEventType.AUTH_FAILURE;
        };
    }

}
