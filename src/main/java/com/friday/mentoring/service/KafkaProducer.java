package com.friday.mentoring.service;

import com.friday.mentoring.dto.AuthEventDto;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Отправляет сообщения в Kafka
 */
@Service
public class KafkaProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    @Value(value = "${siem.events.topic}")
    private String authEventsTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AdminClient adminClient;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate, AdminClient adminClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.adminClient = adminClient;
    }

    /**
     * Отправка события в Кафку
     *
     * @return true если событие было отправлено, иначе false
     */
    public boolean sendAuthEvent(AuthEventDto authEvent) {
        if (kafkaIsActive()) {
            LOGGER.info("Sending message [{}] to Kafka", authEvent);
            try {
                kafkaTemplate.send(authEventsTopic, authEvent).get(3, TimeUnit.SECONDS);
                LOGGER.info("Message was sent");
                return true;
            } catch (Exception ex) {
                LOGGER.warn("Got exception when sending message to Kafka", ex);
            }
        } else {
            LOGGER.info("Auth event [{}] won't be sent to Kafka - Kafka is inactive", authEvent);
        }
        return false;
    }

    private boolean kafkaIsActive() {
        try {
            adminClient.listTopics(new ListTopicsOptions()).listings().get();
            return true;
        } catch (Exception ex) {
            LOGGER.error("Timeout while checking Kafka's availability", ex);
        }

        return false;
    }

}
