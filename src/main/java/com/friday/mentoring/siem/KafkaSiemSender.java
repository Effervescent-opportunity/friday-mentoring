package com.friday.mentoring.siem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.friday.mentoring.todo.SiemEventType;
import com.friday.mentoring.usecase.SiemSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

@Service
class KafkaSiemSender implements SiemSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSiemSender.class);

    @Value(value = "${siem.events.topic}")
    private String authEventsTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaSiemSender(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Отправка события в Кафку
     *
     * @return true если событие было отправлено, иначе false
     */
    @Override
    public boolean send(String ipAddress, OffsetDateTime time, String userName, SiemEventType eventType) {
        try {
            var eventDto = new AuthEventDto(ipAddress, time, userName, eventType);//todo del comment headers are empty now: {}
            kafkaTemplate.send(authEventsTopic, objectMapper.writeValueAsString(eventDto)).get(3, TimeUnit.SECONDS);
            return true;
        } catch (Exception ex) {
            LOGGER.warn("Got exception when sending [{}] message to Kafka", eventType, ex);
        }
        return false;
    }

    private record AuthEventDto(String ipAddress, OffsetDateTime time, String userName, SiemEventType eventType) {
    }
}
