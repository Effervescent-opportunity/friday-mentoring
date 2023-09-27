package com.friday.mentoring.service;

import com.friday.mentoring.event.repository.internal.AuthEventEntity;
import com.friday.mentoring.event.repository.AuthEventReader;
import com.friday.mentoring.event.repository.AuthEventSaver;
import com.friday.mentoring.siem.integration.SiemSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Сервис, отправляющий события в siem
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

    //хаха у меня уже правильно сделано fixedDelay - 1 минута между окончанием прошлого и началом следующего запуска, можно не париться про
    //размер стрима
    @Scheduled(timeUnit = TimeUnit.MINUTES, fixedDelay = 1L)
    public void sendToSiem() {
        try (Stream<AuthEventEntity> stream = authEventReader.getNotSentEvents()) {
            stream.forEach(authEventEntity -> {
                if (siemSender.send(authEventEntity.getIpAddress(), authEventEntity.getEventTime(),
                        //todo correct enum
                        authEventEntity.getUserName(), SiemSender.SiemEventType.valueOf(authEventEntity.getEventType()))) {
                    authEventSaver.setSuccessStatus(authEventEntity.getId());
                }
            });
        } catch (Exception ex) {
            LOGGER.warn("Got exception while sending events to SIEM", ex);
        }

        //todo entities should know nothing about dtos

    }

}
