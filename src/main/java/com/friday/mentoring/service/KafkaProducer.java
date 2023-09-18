package com.friday.mentoring.service;

import com.friday.mentoring.dto.AuthEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Отправляет сообщения в Kafka
 */
@Service
public class KafkaProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    @Value(value = "${siem.events.topic}")
    private String authEventsTopic;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Отправка события в Кафку
     *
     * @return true если событие было отправлено, иначе false
     */
    public boolean sendAuthEvent(AuthEventDto authEvent) {
        try {
            kafkaTemplate.send(authEventsTopic, authEvent).get(3, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            LOGGER.error("Не в силах доставить сообщение в Kafka", e);
        }
        return false;
    }

}
