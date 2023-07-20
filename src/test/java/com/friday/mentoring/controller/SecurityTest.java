package com.friday.mentoring.controller;

import com.friday.mentoring.service.ClockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {

    @MockBean
    ClockService clockService;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        TestSecurityContextHolder.clearContext();
    }

    @Test
    void successfulAuthTest() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"root\", \"password\": \"password\"}"))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    void unSuccessfulAuthTest() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"noRoot\", \"password\": \"password\"}"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }

    @Test
    @WithMockUser(value = "root", roles = "ADMIN")
    void clockSuccessfulAuthTest() throws Exception {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        Mockito.when(clockService.getNowInUtc()).thenReturn(now);

        mockMvc.perform(get("/time/current/utc")).andExpectAll(
                status().isOk(),
                content().contentType("application/json")
        ).andDo(print());

        mockMvc.perform(get("/time/current").param("timezone", "Europe/Paris"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json")
                ).andDo(print());
    }

    @Test
    void clockNotFoundTest() throws Exception {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        Mockito.when(clockService.getNowInUtc()).thenReturn(now);

        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isNotFound()).andDo(print());

        mockMvc.perform(get("/time/current").param("timezone", "Europe/Paris"))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @WithMockUser(value = "noRoot")
    void clockUnsuccessfulAuthTest() throws Exception {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        Mockito.when(clockService.getNowInUtc()).thenReturn(now);

        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isForbidden()).andDo(print());

        mockMvc.perform(get("/time/current").param("timezone", "Europe/Paris"))
                .andExpect(status().isForbidden()).andDo(print());
    }

}
