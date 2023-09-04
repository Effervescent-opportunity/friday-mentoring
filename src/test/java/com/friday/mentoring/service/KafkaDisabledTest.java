package com.friday.mentoring.service;

import com.friday.mentoring.BaseIntegrationTest;
import com.friday.mentoring.db.entity.AuthEventEntity;
import com.friday.mentoring.db.entity.OutboxEntity;
import com.friday.mentoring.db.repository.AuthEventRepository;
import com.friday.mentoring.db.repository.OutboxRepository;
import com.friday.mentoring.dto.AuthEventDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static com.friday.mentoring.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тест KafkaProducer и базы при отсутствии доступа к Кафке
 */
@AutoConfigureMockMvc
public class KafkaDisabledTest extends BaseIntegrationTest {//todo this is integrationTest

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AuthEventRepository authEventRepository;
    @Autowired
    OutboxRepository outboxRepository;
    @MockBean
    KafkaTemplate<String, Object> kafkaTemplate;

    @AfterEach
    void tearDown() {
        authEventRepository.deleteAll();
        outboxRepository.deleteAll();
    }

    @Test
    public void authenticationSuccessTest() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"root\", \"password\": \"password\"}"))
                .andExpect(status().isOk()).andDo(print());

        checkDatabase(AUTHENTICATION_SUCCESS_TYPE, ROOT_USERNAME, LOCAL_IP_ADDRESS, now);
        Mockito.verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void authenticationFailureTest() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"noRoot\", \"password\": \"password\"}"))
                .andExpect(status().isUnauthorized()).andDo(print());

        checkDatabase(AUTHENTICATION_FAILURE_TYPE, "noRoot", LOCAL_IP_ADDRESS, now);
        Mockito.verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    @WithMockUser(value = "root", roles = "ADMIN")
    void authorizationSuccessTest() throws Exception {
        mockMvc.perform(get("/time/current/utc")).andExpectAll(
                status().isOk(),
                content().contentType("application/json")
        ).andDo(print());

        assertEquals(0, authEventRepository.count());
        assertEquals(0, outboxRepository.count());
        Mockito.verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    @WithMockUser(value = "noRoot")
    void authorizationFailureTest() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isForbidden()).andDo(print());

        checkDatabase(AUTHORIZATION_FAILURE_TYPE, "noRoot", "Unknown", now);
        Mockito.verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void authorizationFailureAnonymousUserTest() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isNotFound()).andDo(print());

        checkDatabase(AUTHORIZATION_FAILURE_TYPE, "anonymousUser", LOCAL_IP_ADDRESS, now);
        Mockito.verify(kafkaTemplate, never()).send(anyString(), any());
    }

    private void checkDatabase(String eventType, String userName, String ipAddress, OffsetDateTime startDate) {
        List<AuthEventEntity> authEventEntities = authEventRepository.findAll();
        assertEquals(1, authEventEntities.size());
        AuthEventEntity authEventEntity = authEventEntities.get(0);

        assertEquals(eventType, authEventEntity.getEventType());
        assertEquals(ipAddress, authEventEntity.getIpAddress());
        assertEquals(userName, authEventEntity.getUserName());
        assertTrue(startDate.isBefore(authEventEntity.getEventTime()));
        assertTrue(OffsetDateTime.now().isAfter(authEventEntity.getEventTime()));

        List<OutboxEntity> outboxEntities = outboxRepository.findAll();
        assertEquals(1, outboxEntities.size());
        OutboxEntity outboxEntity = outboxEntities.get(0);

        assertEquals(4, outboxEntity.getRetryCount());
        assertTrue(startDate.isBefore(outboxEntity.getCreatedAt()));
        assertTrue(OffsetDateTime.now().isAfter(outboxEntity.getCreatedAt()));

        AuthEventDto authEventDto = outboxEntity.getEvent();

        assertEquals(eventType, authEventDto.type());
        assertEquals(ipAddress, authEventDto.ipAddress());
        assertEquals(userName, authEventDto.userName());

        assertTrue(startDate.isBefore(authEventDto.time()));
        assertTrue(OffsetDateTime.now().isAfter(authEventDto.time()));
    }
}
