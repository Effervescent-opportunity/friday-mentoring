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
    public NewTopic authEventTopic() {
        return TopicBuilder.name(authEventsTopic).build();
    }

    @Bean
    public AdminClient adminClient() {
        //this creates adminclient-1, it's strange - look at log1 & log2/ and then they both looks at
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 2000);
        return AdminClient.create(configs);
    }

    //2023-08-15T07:57:07.997+03:00 DEBUG 6035 --- [           main] o.a.k.c.a.i.AdminMetadataManager         : [AdminClient clientId=adminclient-2] Setting bootstrap cluster metadata Cluster(id = null, nodes = [localhost:29092 (id: -1 rack: null)], partitions = [], controller = null).
    //todo and then https://stackoverflow.com/questions/37920923/how-to-check-whether-kafka-server-is-running?rq=4
    //https://www.baeldung.com/apache-kafka-check-server-is-running

}
