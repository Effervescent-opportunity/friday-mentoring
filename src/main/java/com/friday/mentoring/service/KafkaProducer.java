package com.friday.mentoring.service;

import com.friday.mentoring.dto.AuthEventDto;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
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

    //todo enable idempotence org.apache.kafka.clients.producer.ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG

    public void sendAuthEvent(AuthEventDto authEvent) {
        if (kafkaIsActive()) {
            LOGGER.info("Sending message [{}] to Kafka", authEvent);

//            try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(authEventsTopic, authEvent);

            SendResult<String, Object> result;

            try {
                result = future.get(3, TimeUnit.SECONDS);
                LOGGER.info("Send Result : {}", result);
                LOGGER.info("Saving entry in db");
                LOGGER.info("Message was sent");
//                    messageRepo.save(result.getProducerRecord().key().toString(),result.getProducerRecord().value().toString());
            } catch (Exception ex) {
                LOGGER.warn("Got exception when sending message to Kafka", ex);
//                    LOGGER.error("Error publishing message ", e);
//                    messageRepo.save(result.getProducerRecord().key().toString(),result.getProducerRecord().value().toString());
            }

            //todo thing for timeouts - here I have 3 seconds, there is "max.block.ms" property which could be useful
            // https://stackoverflow.com/questions/60725906/how-to-verify-sprng-kafka-producer-has-successfully-sent-message-or-not

//                this.kafkaTemplate.send(authEventsTopic, authEvent).get(3, TimeUnit.SECONDS);
//            } catch (Exception ex) {
//                LOGGER.warn("Got exception when sending message to Kafka", ex);
//            }
        } else {
            LOGGER.info("Auth event [{}] won't be sent to Kafka - Kafka is inactive", authEvent);
        }
    }

    private boolean kafkaIsActive() {
        try {
            adminClient.listTopics(new ListTopicsOptions()).listings().get();
            return true;
        } catch (ExecutionException | InterruptedException ex) {
            LOGGER.error("Timeout while checking Kafka's availability", ex);
        }

        return false;
    }

}
