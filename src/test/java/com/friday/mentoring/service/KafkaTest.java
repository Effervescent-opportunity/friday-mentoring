package com.friday.mentoring.service;

import com.friday.mentoring.dto.AuthEventDto;
import com.friday.mentoring.util.AuthEventDtoDeserializer;
import org.apache.kafka.clients.consumer.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EmbeddedKafka(ports = {29092}, zkSessionTimeout = 3000, zkConnectionTimeout = 2000, adminTimeout = 1, partitions = 1)
@SpringBootTest
@AutoConfigureMockMvc
public class KafkaTest {

//    @Autowired
//    private KafkaTemplate<String, String> template;

    //    @Value(value = "${mentoring.auth.events.topic}")
    static String authEventsTopic = "mentoring.auth.events";

    //@Autowired
    static MockConsumer<String, Object> mockConsumer;
    @Autowired
    MockMvc mockMvc;

//    @Autowired
//    EmbeddedKafkaBroker embeddedKafkaBroker;

//    static Consumer<String, Object> consumer;

//    Map<String, Object> consumerProps;

//    @BeforeAll
//    private


//    @BeforeEach
//    void setUp() {
//        consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
////        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
//        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AuthEventDtoDeserializer.class);
//        System.out.println("LALALA3 " + consumerProps.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
//
//        consumer = new DefaultKafkaConsumerFactory<String, Object>(consumerProps).createConsumer();
//        consumer.subscribe(Collections.singletonList(authEventsTopic));
//    }

    @AfterEach
    void tearDown() {

//        consumer.close();
        TestSecurityContextHolder.clearContext();
    }

    @BeforeAll
    static void beforeAll() {
        mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        mockConsumer.subscribe(Collections.singletonList(authEventsTopic));
//        EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaBroker(1);
//        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
////        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
//        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AuthEventDtoDeserializer.class);
//
//        consumer = new DefaultKafkaConsumerFactory<String, Object>(consumerProps).createConsumer();
//        consumer.subscribe(Collections.singletonList(authEventsTopic));
    }

    @AfterAll
    static void afterAll() {
        mockConsumer.close();
//        consumer.close();
    }

//todo this doesn't work((

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

//    public class AuthEventDtoDeserializer extends JsonDeserializer<AuthEventDto> {
//        public AuthEventDtoDeserializer() {
//            super(AuthEventDto.class);
//        }
//    }

    @Test
    public void authenticationSuccessTest() throws Exception {
//        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
//        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AuthEventDtoDeserializer.class);
//        Consumer<String, Object> consumer = new DefaultKafkaConsumerFactory<String, Object>(consumerProps).createConsumer();
//        consumer.subscribe(Collections.singletonList(authEventsTopic));

        LocalDateTime now = LocalDateTime.now();

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"root\", \"password\": \"password\"}"))
                .andExpect(status().isOk()).andDo(print());

        ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(mockConsumer, authEventsTopic);
//        ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(consumer, authEventsTopic);
        System.out.println("LALALA2 record: " + singleRecord);
        assertNotNull(singleRecord);
        System.out.println("LALALA2 record value: " + singleRecord.value());
        //AuthEventDto[ipAddress=127.0.0.1, time=2023-08-04T08:20:51.486142584, userName=root, type=AUTHENTICATION_SUCCESS]
        //{"ipAddress":"127.0.0.1","time":"2023-08-04T08:10:34.965668006","userName":"root","type":"AUTHENTICATION_SUCCESS"}
        checkAuthEventDto(singleRecord.value(), "root", "AUTHENTICATION_SUCCESS", now);
//        assertTrue(singleRecord.value() instanceof AuthEventDto);
//        AuthEventDto authEventDto = (AuthEventDto) singleRecord.value();
//        assertEquals("127.0.0.1", authEventDto.ipAddress());
//        assertEquals("root", authEventDto.userName());
//        assertEquals("AUTHENTICATION_SUCCESS", authEventDto.type());
//
//        assertTrue(now.isBefore(authEventDto.time()));
//        assertTrue(LocalDateTime.now().isAfter(authEventDto.time()));

//        assertThat(singleRecord.value()).isEqualTo("my-test-value");

//        consumer.close();
    }

    @Test
    void authenticationFailureTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"noRoot\", \"password\": \"password\"}"))
                .andExpect(status().isUnauthorized()).andDo(print());

        ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(mockConsumer, authEventsTopic);
//        ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(consumer, authEventsTopic);
        System.out.println("LALALA2 record: " + singleRecord);
        assertNotNull(singleRecord);
        System.out.println("LALALA2 record value: " + singleRecord.value());
        //AuthEventDto[ipAddress=127.0.0.1, time=2023-08-04T08:20:51.486142584, userName=root, type=AUTHENTICATION_SUCCESS]
        //{"ipAddress":"127.0.0.1","time":"2023-08-04T08:10:34.965668006","userName":"root","type":"AUTHENTICATION_SUCCESS"}
        checkAuthEventDto(singleRecord.value(), "noRoot", "AUTHENTICATION_FAILURE", now);
    }

    @Test
    @WithMockUser(value = "root", roles = "ADMIN")
    void authorizationSuccessTest() throws Exception {
        mockMvc.perform(get("/time/current/utc")).andExpectAll(
                status().isOk(),
                content().contentType("application/json")
        ).andDo(print());

        assertThrows(IllegalStateException.class, () -> KafkaTestUtils.getRecords(mockConsumer, Duration.ofSeconds(4)));
//        assertThrows(IllegalStateException.class, () -> KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(4)));
    }

    @Test
    @WithMockUser(value = "noRoot")
    void authorizationFailureTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isForbidden()).andDo(print());

        ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(mockConsumer, authEventsTopic);
//        ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(consumer, authEventsTopic);

        assertNotNull(singleRecord);
        System.out.println("LALALA2 record value: " + singleRecord.value());
        checkAuthEventDto(singleRecord.value(), "noRoot", "AUTHORIZATION_FAILURE", now);
    }

    @Test
    void authorizationFailureAnonymousUserTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isNotFound()).andDo(print());

        ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(mockConsumer, authEventsTopic);
//        ConsumerRecord<String, Object> singleRecord = KafkaTestUtils.getSingleRecord(consumer, authEventsTopic);

        assertNotNull(singleRecord);
        System.out.println("LALALA2 record value: " + singleRecord.value());
        checkAuthEventDto(singleRecord.value(), "anonymousUser", "AUTHORIZATION_FAILURE", now);
    }


    private void checkAuthEventDto(Object valueFromKafka, String userName, String authType, LocalDateTime startDate) {
        assertTrue(valueFromKafka instanceof AuthEventDto);
        AuthEventDto authEventDto = (AuthEventDto) valueFromKafka;
        assertEquals("127.0.0.1", authEventDto.ipAddress());
        assertEquals(userName, authEventDto.userName());
        assertEquals(authType, authEventDto.type());

        assertTrue(startDate.isBefore(authEventDto.time()));
        assertTrue(LocalDateTime.now().isAfter(authEventDto.time()));
    }

    //

}
