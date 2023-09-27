package com.friday.mentoring.service;

import com.friday.mentoring.db.entity.AuthEventEntity;
import com.friday.mentoring.db.entity.OutboxEntity;
import com.friday.mentoring.db.repository.AuthEventRepository;
import com.friday.mentoring.db.repository.OutboxRepository;
import com.friday.mentoring.dto.AuthEventDto;
import com.friday.mentoring.event.repository.AuthEventReader;
import com.friday.mentoring.event.repository.AuthEventSaver;
import com.friday.mentoring.siem.integration.SiemSender;
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
    public void retrySendingToKafka() {
        List<AuthEventEntity> authEventEntities = authEventRepository.findAllByWasSentFalse();

        for (AuthEventEntity authEventEntity : authEventEntities) {
            if (siemSender.send(authEventEntity.getIpAddress(), authEventEntity.getEventTime(),
                            authEventEntity.getUserName(), authEventEntity.getEventType()))) {//todo enum!
                //entities should know nothing about dtos
                authEventSaver.setSuccessStatus(authEventEntity.getId());
                LOGGER.debug("AuthEventEntity [{}] was sent to Siem", authEventEntity);
            }
        }
    }

}
