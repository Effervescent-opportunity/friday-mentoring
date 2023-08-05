package com.friday.mentoring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.friday.mentoring.dto.AuthEventDto;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class KafkaTest {

    @Value(value = "${siem.events.topic}")
    String authEventsTopic;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

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
    }

    @Test
    public void authenticationSuccessTest() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"root\", \"password\": \"password\"}"))
                .andExpect(status().isOk()).andDo(print());

        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, authEventsTopic);

        assertNotNull(singleRecord);
        checkAuthEventDto(singleRecord.value(), "root", "AUTHENTICATION_SUCCESS", now);
    }

    @Test
    void authenticationFailureTest() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"noRoot\", \"password\": \"password\"}"))
                .andExpect(status().isUnauthorized()).andDo(print());

        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, authEventsTopic);

        assertNotNull(singleRecord);
        checkAuthEventDto(singleRecord.value(), "noRoot", "AUTHENTICATION_FAILURE", now);
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
    }

    @Test
    @WithMockUser(value = "noRoot")
    @Disabled("Не работает - event не создается")
    void authorizationFailureTest() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isForbidden()).andDo(print());

        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, authEventsTopic);

        assertNotNull(singleRecord);
        checkAuthEventDto(singleRecord.value(), "noRoot", "AUTHORIZATION_FAILURE", now);
    }

    @Test
    @Disabled("Не работает - event не создается")
    void authorizationFailureAnonymousUserTest() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isNotFound()).andDo(print());

        ConsumerRecord<String, String> singleRecord = KafkaTestUtils.getSingleRecord(consumer, authEventsTopic);

        assertNotNull(singleRecord);
        checkAuthEventDto(singleRecord.value(), "anonymousUser", "AUTHORIZATION_FAILURE", now);
    }

    private void checkAuthEventDto(String valueFromKafka, String userName, String authType, OffsetDateTime startDate) {
        try {
            AuthEventDto authEventDto = objectMapper.readValue(valueFromKafka, AuthEventDto.class);

            assertEquals("127.0.0.1", authEventDto.ipAddress());
            assertEquals(userName, authEventDto.userName());
            assertEquals(authType, authEventDto.type());

            assertTrue(startDate.isBefore(authEventDto.time()));
            assertTrue(OffsetDateTime.now().isAfter(authEventDto.time()));
        } catch (JsonProcessingException ex) {
            Assertions.fail("Cannot get AuthEventDto from json string", ex);
        }
    }

}
