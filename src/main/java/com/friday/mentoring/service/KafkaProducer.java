package com.friday.mentoring.service;

import com.friday.mentoring.dto.AuthEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class KafkaProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);
    private static final String TOPIC = "mentoring.auth.events";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAuthEvent(AuthEventDto authEvent) {
        LOGGER.info("Sending message [{}] to Kafka", authEvent);


        LOGGER.info("Message was sent");
    }

    public void sendMessage(String message) {
        LOGGER.info("#### -> Producing message -> [{}]", message);
        this.kafkaTemplate.send(TOPIC, message);
    }
}
