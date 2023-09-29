package com.friday.mentoring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.friday.mentoring.BaseIntegrationTest;
import com.friday.mentoring.event.AuthEventType;
import com.friday.mentoring.event.repository.internal.AuthEventEntity;
import com.friday.mentoring.event.repository.internal.AuthEventRepository;
import com.friday.mentoring.siem.integration.SiemEventType;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.friday.mentoring.event.AuthEventType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тест KafkaProducer и базы при наличии Кафки
 */
@EmbeddedKafka(ports = {29093}, zkSessionTimeout = 3000, zkConnectionTimeout = 2000, adminTimeout = 1, partitions = 1)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class KafkaTest extends BaseIntegrationTest {

    static final String LOCAL_IP_ADDRESS = "127.0.0.1";

    @Value(value = "${siem.events.topic}")
    String authEventsTopic;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    AuthEventRepository authEventRepository;
    Consumer<String, String> consumer;
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);

        consumer = new DefaultKafkaConsumerFactory<String, String>(consumerProps).createConsumer();
        consumer.subscribe(Collections.singletonList(authEventsTopic));
    }

    @AfterEach
    void tearDown() {
        consumer.close();
        authEventRepository.deleteAll();
    }

    @Test
    public void authenticationSuccessTest() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"root\", \"password\": \"password\"}"))
                .andExpect(status().isOk()).andDo(print());

        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, authEventsTopic);

        assertNotNull(singleRecord);
        checkRecordedValues(singleRecord.value(), LOCAL_IP_ADDRESS, "root", AUTHENTICATION_SUCCESS, now);
    }

    @Test
    void authenticationFailureTest() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"noRoot\", \"password\": \"password\"}"))
                .andExpect(status().isUnauthorized()).andDo(print());

        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, authEventsTopic);

        assertNotNull(singleRecord);
        checkRecordedValues(singleRecord.value(), LOCAL_IP_ADDRESS, "noRoot", AUTHENTICATION_FAILURE, now);
    }

    @Test
    @WithMockUser(value = "root", roles = "ADMIN")
    void authorizationSuccessTest() throws Exception {
        mockMvc.perform(get("/time/current/utc")).andExpectAll(
                status().isOk(),
                content().contentType("application/json")
        ).andDo(print());

        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(4));
        assertEquals(0, records.count());
        assertEquals(0, authEventRepository.count());
    }

    @Test
    @WithMockUser(value = "noRoot")
    void authorizationFailureTest() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isForbidden()).andDo(print());

        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, authEventsTopic);

        assertNotNull(singleRecord);
        checkRecordedValues(singleRecord.value(), "Unknown", "noRoot", AUTHORIZATION_FAILURE, now);
    }

    @Test
    void authorizationFailureAnonymousUserTest() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isNotFound()).andDo(print());

        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, authEventsTopic);


        assertNotNull(singleRecord);
        checkRecordedValues(singleRecord.value(), LOCAL_IP_ADDRESS, "anonymousUser", AUTHORIZATION_FAILURE, now);
    }

    private void checkRecordedValues(String valueFromKafka, String ipAddress, String userName, AuthEventType authType, OffsetDateTime startDate) {
        try {
            AuthEventDto authEventDto = objectMapper.readValue(valueFromKafka, AuthEventDto.class);
            SiemEventType siemEventType = authType == AUTHENTICATION_SUCCESS ? SiemEventType.AUTH_SUCCESS : SiemEventType.AUTH_FAILURE;

            assertEquals(ipAddress, authEventDto.ipAddress());
            assertEquals(userName, authEventDto.userName());
            assertEquals(siemEventType, authEventDto.eventType());

            assertTrue(startDate.isBefore(authEventDto.time()));
            assertTrue(OffsetDateTime.now().isAfter(authEventDto.time()));

            List<AuthEventEntity> authEventEntities = authEventRepository.findAll();
            assertEquals(1, authEventEntities.size());
            AuthEventEntity authEventEntity = authEventEntities.get(0);

            assertEquals(ipAddress, authEventEntity.getIpAddress());
            assertEquals(userName, authEventEntity.getUserName());
            assertEquals(authType, authEventEntity.getEventType());

            assertTrue(startDate.isBefore(authEventEntity.getEventTime()));
            assertTrue(OffsetDateTime.now().isAfter(authEventEntity.getEventTime()));
            assertEquals(authEventDto.time().truncatedTo(ChronoUnit.MILLIS), authEventEntity.getEventTime().truncatedTo(ChronoUnit.MILLIS));
        } catch (JsonProcessingException ex) {
            Assertions.fail("Cannot get AuthEventDto from json string", ex);
        }
    }

    private record AuthEventDto(String ipAddress, OffsetDateTime time, String userName, SiemEventType eventType) {
    }

}
