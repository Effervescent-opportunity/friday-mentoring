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

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"root\", \"password\": \"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());

        checkDatabase(TestConstants.AUTHENTICATION_SUCCESS_TYPE, TestConstants.ROOT_USERNAME, TestConstants.LOCAL_IP_ADDRESS, now);
        Mockito.verify(kafkaTemplate, Mockito.never()).send(ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    void authenticationFailureTest() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"noRoot\", \"password\": \"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized()).andDo(MockMvcResultHandlers.print());

        checkDatabase(TestConstants.AUTHENTICATION_FAILURE_TYPE, "noRoot", TestConstants.LOCAL_IP_ADDRESS, now);
        Mockito.verify(kafkaTemplate, Mockito.never()).send(ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    @WithMockUser(value = "root", roles = "ADMIN")
    void authorizationSuccessTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/time/current/utc")).andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentType("application/json")
        ).andDo(MockMvcResultHandlers.print());

        Assertions.assertEquals(0, authEventRepository.count());
        Assertions.assertEquals(0, outboxRepository.count());
        Mockito.verify(kafkaTemplate, Mockito.never()).send(ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    @WithMockUser(value = "noRoot")
    void authorizationFailureTest() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        mockMvc.perform(MockMvcRequestBuilders.get("/time/current/utc"))
                .andExpect(MockMvcResultMatchers.status().isForbidden()).andDo(MockMvcResultHandlers.print());

        checkDatabase(TestConstants.AUTHORIZATION_FAILURE_TYPE, "noRoot", "Unknown", now);
        Mockito.verify(kafkaTemplate, Mockito.never()).send(ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    void authorizationFailureAnonymousUserTest() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        mockMvc.perform(MockMvcRequestBuilders.get("/time/current/utc"))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andDo(MockMvcResultHandlers.print());

        checkDatabase(TestConstants.AUTHORIZATION_FAILURE_TYPE, "anonymousUser", TestConstants.LOCAL_IP_ADDRESS, now);
        Mockito.verify(kafkaTemplate, Mockito.never()).send(ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    private void checkDatabase(String eventType, String userName, String ipAddress, OffsetDateTime startDate) {
        List<AuthEventEntity> authEventEntities = authEventRepository.findAll();
        Assertions.assertEquals(1, authEventEntities.size());
        AuthEventEntity authEventEntity = authEventEntities.get(0);

        Assertions.assertEquals(eventType, authEventEntity.getEventType());
        Assertions.assertEquals(ipAddress, authEventEntity.getIpAddress());
        Assertions.assertEquals(userName, authEventEntity.getUserName());
        Assertions.assertTrue(startDate.isBefore(authEventEntity.getEventTime()));
        Assertions.assertTrue(OffsetDateTime.now().isAfter(authEventEntity.getEventTime()));

        List<OutboxEntity> outboxEntities = outboxRepository.findAll();
        Assertions.assertEquals(1, outboxEntities.size());
        OutboxEntity outboxEntity = outboxEntities.get(0);

        Assertions.assertEquals(4, outboxEntity.getRetryCount());
        Assertions.assertTrue(startDate.isBefore(outboxEntity.getCreatedAt()));
        Assertions.assertTrue(OffsetDateTime.now().isAfter(outboxEntity.getCreatedAt()));

        AuthEventDto authEventDto = outboxEntity.getEvent();

        Assertions.assertEquals(eventType, authEventDto.type());
        Assertions.assertEquals(ipAddress, authEventDto.ipAddress());
        Assertions.assertEquals(userName, authEventDto.userName());

        Assertions.assertTrue(startDate.isBefore(authEventDto.time()));
        Assertions.assertTrue(OffsetDateTime.now().isAfter(authEventDto.time()));
    }
}
