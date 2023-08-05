package com.friday.mentoring.service;

import com.friday.mentoring.dto.AuthEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Value(value = "${mentoring.auth.events.topic}")
    private String authEventsTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendAuthEvent(AuthEventDto authEvent) {
        if (kafkaIsActive()) {
            LOGGER.info("Sending message [{}] to Kafka", authEvent);

            try {
                this.kafkaTemplate.send(authEventsTopic, authEvent).get(3, TimeUnit.SECONDS);
                LOGGER.info("Message was sent");
            } catch (Exception ex) {
                LOGGER.warn("Got exception when sending message to Kafka", ex);
            }
        } else {
            LOGGER.info("Auth event [{}] won't be sent to Kafka - Kafka is inactive", authEvent);
        }
    }

    private boolean kafkaIsActive() {
        return true;
    }

}
