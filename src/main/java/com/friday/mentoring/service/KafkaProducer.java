package com.friday.mentoring.service;

import com.friday.mentoring.dto.AuthEventDto;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class KafkaProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);
    private static final String TOPIC = "mentoring.auth.events";

    private final AdminClient adminClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducer(AdminClient adminClient, KafkaTemplate<String, Object> kafkaTemplate) {
        this.adminClient = adminClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAuthEvent(AuthEventDto authEvent) {
        if (kafkaIsActive()) {
            LOGGER.info("Sending message [{}] to Kafka", authEvent);
            this.kafkaTemplate.send(TOPIC, authEvent);//todo try-catch and timeout
            LOGGER.info("Message was sent");
        } else {
            LOGGER.info("Auth event [{}] won't be sent to Kafka - Kafka is inactive", authEvent);
        }
    }

    private boolean kafkaIsActive() {
        try {
            Collection<Node> nodes = adminClient.describeCluster().nodes().get();
            return nodes != null && nodes.size() > 0;
        } catch (Exception ex) {
            return false;
        }
    }

//    public void sendMessage(String message) {
//        LOGGER.info("#### -> Producing message -> [{}]", message);
//        this.kafkaTemplate.send(TOPIC, message);
//    }
}
