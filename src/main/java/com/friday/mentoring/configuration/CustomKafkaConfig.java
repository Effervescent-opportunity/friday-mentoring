package com.friday.mentoring.configuration;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CustomKafkaConfig {
    //todo why in adminclient config I have timeout 30000 o.a.k.clients.admin.AdminClientConfig    : AdminClientConfig values:
//todo wtf is 2023-08-03T17:52:30.804+03:00  WARN 15881 --- [| adminclient-2] org.apache.kafka.clients.NetworkClient   : [AdminClient clientId=adminclient-2] Connection to node -1 (localhost/127.0.0.1:9092) could not be established. Broker may not be available.
//    @Value(value = "${spring.kafka.producer.bootstrap-servers}")
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    //todo why I have 2 2023-08-03T18:10:35.615+03:00  INFO 24720 --- [           main] o.a.k.clients.admin.AdminClientConfig    : AdminClientConfig values:
    //and
    // //todo properties - this is active spring.kafka.admin.properties.request.timeout.ms=1000
//    @Bean
//    public KafkaAdmin kafkaAdmin() {//for creating topics, automatically with spring boot
//        Map<String, Object> configs = new HashMap<>();
//        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
//        return new KafkaAdmin(configs);
//    }

//    @Bean
    //this created incorrect admin client - or I had to add timeout here
    //
//    public AdminClient adminClient() {//todo this is not working for checking kafka's ability
//        Map<String, Object> configs = new HashMap<>();
//        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
////        configs.put("request.timeout.ms", 3000);
////        configs.put("connections.max.idle.ms", 5000);
//        return AdminClient.create(configs);
//    }

    @Bean
    public NewTopic authEventTopic() {//может перенести это в конфигурацию просто?
        return TopicBuilder.name("mentoring.auth.events").build();
    }

}
