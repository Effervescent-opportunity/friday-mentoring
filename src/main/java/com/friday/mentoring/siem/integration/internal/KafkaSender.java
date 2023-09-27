package com.friday.mentoring.siem.integration.internal;

import com.friday.mentoring.siem.integration.SiemSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

@Service
class KafkaSender implements SiemSender {
/*
todo fix time - maybe it's because earlier I had events from jsonb from db and now just from db where they are stored in UTC
now I have headers: {
	"__TypeId__": "com.friday.mentoring.siem.integration.internal.KafkaSender$AuthEventDto"
} and time "time": "2023-09-27T20:00:24.697264Z",
earlier:{
	"__TypeId__": "com.friday.mentoring.dto.AuthEventDto"
}
and time was "time": "2023-09-21T22:44:38.625556507+03:00",
 */
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSender.class);

    @Value(value = "${siem.events.topic}")
    private String authEventsTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaSender(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Отправка события в Кафку
     *
     * @return true если событие было отправлено, иначе false
     */
    @Override
    public boolean send(String ipAddress, OffsetDateTime time, String userName, SiemEventType eventType) {
        try {
            kafkaTemplate.send(authEventsTopic, new AuthEventDto(ipAddress, time, userName, eventType)).get(3, TimeUnit.SECONDS);
            return true;
        } catch (Exception ex) {
            LOGGER.warn("Got exception when sending [{}] message to Kafka", eventType, ex);
        }
        return false;
    }

    private record AuthEventDto(String ipAddress, OffsetDateTime time, String userName, SiemEventType eventType) {
    }
}
