package com.friday.mentoring.service;

import com.friday.mentoring.dto.AuthEventDto;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Отправляет сообщения в топик
 */
@Service
public class KafkaProducer {//todo let's stop trying find out how to check if Kafka is available and just leave this bad thing
    //todo why idea stops containers when I stop application?
    //todo tests
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    @Value(value = "${mentoring.auth.events.topic}")
    private String authEventsTopic;
    private static final String TOPIC = "mentoring.auth.events";

//    private final AdminClient adminClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


//    public KafkaProducer(AdminClient adminClient, KafkaTemplate<String, Object> kafkaTemplate) {
//        this.adminClient = adminClient;
//        this.kafkaTemplate = kafkaTemplate;
//    }

    public void sendAuthEvent(AuthEventDto authEvent) {
        if (kafkaIsActive()) {
            LOGGER.info("Sending message [{}] to Kafka", authEvent);

            try {
                this.kafkaTemplate.send(TOPIC, authEvent).get(3, TimeUnit.SECONDS);
                LOGGER.info("Message was sent");
            } catch (Exception ex) {
                LOGGER.info("Got exception when sending message to Kafka", ex);
            }
        } else {
            LOGGER.info("Auth event [{}] won't be sent to Kafka - Kafka is inactive", authEvent);
        }
    }

    private boolean kafkaIsActive() {
        return true;
//        try {
//            Collection<Node> nodes = adminClient.describeCluster().nodes().get();
//            return nodes != null && nodes.size() > 0;
//        } catch (Exception ex) {
//            return false;
//        }
    }

//    public void sendMessage(String message) {
//        LOGGER.info("#### -> Producing message -> [{}]", message);
//        this.kafkaTemplate.send(TOPIC, message);
//    }
}
