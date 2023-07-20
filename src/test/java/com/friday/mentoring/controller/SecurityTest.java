package com.friday.mentoring.controller;

import com.friday.mentoring.service.ClockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
    void successfulAuth1Test() throws Exception {
        mockMvc.perform(get("/auth/login1")
                        .param("user", "root").param("password", "password"))
                .andExpectAll(
                        status().isOk()
                ).andDo(print());
    }

    @Test
    void unSuccessfulAuth1Test() throws Exception {
        mockMvc.perform(get("/auth/login1")
                        .param("user", "nonRoot").param("password", "password"))
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

    //If a (proxy) server receives invalid credentials, it should respond with a 401 Unauthorized or
    // with a 407 Proxy Authentication Required, and the user may send a new request or replace the Authorization header field.
    //
    //If a (proxy) server receives valid credentials that are inadequate to access a given resource,
    // the server should respond with the 403 Forbidden status code. Unlike 401 Unauthorized or 407 Proxy Authentication Required, authentication is impossible for this user and browsers will not propose a new attempt.
    //
    //In all cases, the server may prefer returning a 404 Not Found status code, to hide the existence of the page to a user without adequate privileges or not correctly authenticated.

    @Test
    void clockForbiddenTest() throws Exception {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        Mockito.when(clockService.getNowInUtc()).thenReturn(now);

        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isUnauthorized()).andDo(print());

        mockMvc.perform(get("/time/current").param("timezone", "Europe/Paris"))
                .andExpect(status().isUnauthorized()
                ).andDo(print());
    }

    @Test
    @WithMockUser(value = "noRoot")
    void clockUnsuccessfulAuthTest() throws Exception {//todo why 200?
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        Mockito.when(clockService.getNowInUtc()).thenReturn(now);

        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isForbidden()).andDo(print());

        mockMvc.perform(get("/time/current").param("timezone", "Europe/Paris"))
                .andExpect(status().isForbidden()).andDo(print());
    }

}
