package com.friday.mentoring.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @MockBean
    KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    public void authenticationSuccessTest() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"root\", \"password\": \"password\"}"))
                .andExpect(status().isOk()).andDo(print());

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
