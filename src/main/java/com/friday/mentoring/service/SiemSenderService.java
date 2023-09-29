package com.friday.mentoring.service;

import com.friday.mentoring.event.AuthEventType;
import com.friday.mentoring.event.repository.AuthEventReader;
import com.friday.mentoring.event.repository.AuthEventSaver;
import com.friday.mentoring.event.repository.internal.AuthEventEntity;
import com.friday.mentoring.siem.integration.SiemEventType;
import com.friday.mentoring.siem.integration.SiemSender;
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
public class SiemSenderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SiemSenderService.class);

    private final SiemSender siemSender;
    private final AuthEventSaver authEventSaver;
    private final AuthEventReader authEventReader;

    public SiemSenderService(SiemSender siemSender, AuthEventSaver authEventSaver, AuthEventReader authEventReader) {
        this.siemSender = siemSender;
        this.authEventSaver = authEventSaver;
        this.authEventReader = authEventReader;
    }

    @Transactional
    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedDelayString = "${siem.send.fixed.delay.seconds:60}")
    public void sendToSiem() {
        try (Stream<AuthEventEntity> stream = authEventReader.getNotSentEvents()) {
            stream.forEach(authEventEntity -> {
                if (siemSender.send(authEventEntity.getIpAddress(), authEventEntity.getEventTime(),
                        authEventEntity.getUserName(), authEventEntity.getEventType() == AuthEventType.AUTHENTICATION_SUCCESS
                                ? SiemEventType.AUTH_SUCCESS : SiemEventType.AUTH_FAILURE)) {
                    authEventSaver.setSuccessStatus(authEventEntity.getId());
                }
            });
        } catch (Exception ex) {
            LOGGER.warn("Got exception while sending events to SIEM", ex);
        }
    }

}
