package com.friday.mentoring.configuration;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

/**
 * Бины для настройки Кафки
 */
@Configuration
public class CustomKafkaConfig {

    @Value(value = "${siem.events.topic}")
    private String authEventsTopic;

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin.NewTopics authEventTopic(KafkaAdmin admin) {
        admin.setAutoCreate(false);
        return new KafkaAdmin.NewTopics(TopicBuilder.name(authEventsTopic).build());
    }

}
