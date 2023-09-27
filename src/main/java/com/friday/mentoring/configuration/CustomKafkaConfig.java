package com.friday.mentoring.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Бины для настройки Кафки
 */
@Configuration
public class CustomKafkaConfig {//todo make package private and move to siem.integration.internal

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
