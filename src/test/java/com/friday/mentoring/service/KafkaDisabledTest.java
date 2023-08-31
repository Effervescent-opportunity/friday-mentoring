package com.friday.mentoring.service;

import com.friday.mentoring.db.entity.AuthEventEntity;
import com.friday.mentoring.db.entity.OutboxEntity;
import com.friday.mentoring.db.repository.AuthEventRepository;
import com.friday.mentoring.db.repository.OutboxRepository;
//import com.friday.mentoring.util.DbTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тест KafkaProducer при отсутствии доступа к Кафке
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class KafkaDisabledTest {//todo fix

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthEventRepository authEventRepository;

    @Autowired
    OutboxRepository outboxRepository;

//    @Autowired
//    JdbcTemplate jdbcTemplate;

    @MockBean
    KafkaTemplate<String, Object> kafkaTemplate;

    @AfterEach
    void tearDown() {
        authEventRepository.deleteAll();
        outboxRepository.deleteAll();
    }

    @Test
    public void authenticationSuccessTest() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"root\", \"password\": \"password\"}"))
                .andExpect(status().isOk()).andDo(print());

        List<AuthEventEntity> authEventEntities = authEventRepository.findAll();
        assertEquals(1, authEventEntities.size());

        List<OutboxEntity> outboxEntities = outboxRepository.findAll();
        assertEquals(1, outboxEntities.size());
        assertEquals(4, outboxEntities.get(0).getRetryCount());
        assertEquals(authEventEntities.get(0).getType(), outboxEntities.get(0).getEvent().type());

//
//        assertEquals(1, DbTestUtils.getAuthEventCount(jdbcTemplate));
//        assertEquals(1, DbTestUtils.getOutboxCount(jdbcTemplate));
//
//        System.out.println("LOLOLO auth: " + DbTestUtils.getAuthEvents(jdbcTemplate).get(0));
//        System.out.println("LOLOLO outbox: " + DbTestUtils.getOutboxEntities(jdbcTemplate).get(0));

        Mockito.verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void authenticationFailureTest() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"noRoot\", \"password\": \"password\"}"))
                .andExpect(status().isUnauthorized()).andDo(print());

        Mockito.verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    @WithMockUser(value = "root", roles = "ADMIN")
    void authorizationSuccessTest() throws Exception {
        mockMvc.perform(get("/time/current/utc")).andExpectAll(
                status().isOk(),
                content().contentType("application/json")
        ).andDo(print());

        Mockito.verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    @WithMockUser(value = "noRoot")
    void authorizationFailureTest() throws Exception {
        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isForbidden()).andDo(print());

        Mockito.verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void authorizationFailureAnonymousUserTest() throws Exception {
        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isNotFound()).andDo(print());

        Mockito.verify(kafkaTemplate, never()).send(anyString(), any());
    }

}
