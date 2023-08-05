package com.friday.mentoring.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Бины для настройки Кафки
 */
@Configuration
public class CustomKafkaConfig {

    @Value(value = "${mentoring.auth.events.topic}")
    private String authEventsTopic;

    @Bean
    public NewTopic authEventTopic() {
        return TopicBuilder.name(authEventsTopic).build();
    }

}
