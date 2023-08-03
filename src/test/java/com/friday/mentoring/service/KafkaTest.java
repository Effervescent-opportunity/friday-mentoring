package com.friday.mentoring.service;

import com.friday.mentoring.dto.AuthEventDto;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@EmbeddedKafka
@SpringBootTest
public class KafkaTest {

//    MockConsumer<String, String> consume
    @Autowired
    MockMvc mockMvc;
    //todo wtf Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'org.springframework.test.web.servlet.MockMvc' 

//    @BeforeAll
//    private


    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Value(value = "${mentoring.auth.events.topic}")
    String authEventsTopic;

//    @Test
//    public void testReceivingKafkaEvents() {
//        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
//        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        Consumer<String, Object> consumer = new DefaultKafkaConsumerFactory<String, Object>(consumerProps)
//                .createConsumer();
//        consumer.subscribe(Collections.singletonList(authEventsTopic));
//
////        Consumer<Integer, String> consumer = configureConsumer();
////        Producer<Integer, String> producer = configureProducer();
//
////        producer.send(new ProducerRecord<>(TEST_TOPIC, 123, "my-test-value"));
//
//        ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(consumer, authEventsTopic);
//        assertThat(singleRecord).isNotNull();
//        assertThat(singleRecord.key()).isEqualTo(123);
//        assertThat(singleRecord.value()).isEqualTo("my-test-value");
//
//        consumer.close();
////        producer.close();
//    }

//    private Consumer<Integer, String> configureConsumer() {
//        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
//        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        Consumer<Integer, String> consumer = new DefaultKafkaConsumerFactory<Integer, String>(consumerProps)
//                .createConsumer();
//        consumer.subscribe(Collections.singleton(TEST_TOPIC));
//        return consumer;
//    }

    @Test
    public void successfulAuthenticationTest() throws Exception {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        Consumer<String, Object> consumer = new DefaultKafkaConsumerFactory<String, Object>(consumerProps)
                .createConsumer();
        consumer.subscribe(Collections.singletonList(authEventsTopic));

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"root\", \"password\": \"password\"}"))
                .andExpect(status().isOk()).andDo(print());

        ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(consumer, authEventsTopic);
        System.out.println("LALALA2 record: " + singleRecord);
        assertNotNull(singleRecord);
        assertTrue(singleRecord.value() instanceof AuthEventDto);

//        assertThat(singleRecord.value()).isEqualTo("my-test-value");

        consumer.close();
    }
//
//    @Test
//    void unSuccessfulAuthenticationTest() throws Exception {
//        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"user\": \"noRoot\", \"password\": \"password\"}"))
//                .andExpect(status().isUnauthorized()).andDo(print());
//
//
//    }
}
