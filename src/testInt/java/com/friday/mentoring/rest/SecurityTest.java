package com.friday.mentoring.rest;

import com.friday.mentoring.BaseIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@AutoConfigureMockMvc
public class SecurityTest extends BaseIntegrationTest {
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

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"wakeup\", \"password\": \"password\"}"))
                .andExpect(status().isOk()).andDo(print());

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"beza\", \"password\": \"password\"}"))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    void unSuccessfulAuthTest() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"noRoot\", \"password\": \"password\"}"))
                .andExpect(status().isUnauthorized()).andDo(print());
    }

    @Test
    @WithMockUser(roles = "TIME")
    void clockSuccessfulAuthTest() throws Exception {
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
    void clockNoUserNotFoundTest() throws Exception {
        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isNotFound()).andDo(print());

        mockMvc.perform(get("/time/current").param("timezone", "Europe/Paris"))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @WithMockUser(roles = "SECURITY")
    void clockNotTimeRoleForbiddenTest() throws Exception {
        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isForbidden()).andDo(print());

        mockMvc.perform(get("/time/current").param("timezone", "Europe/Paris"))
                .andExpect(status().isForbidden()).andDo(print());
    }

    @Test
    @WithMockUser(roles = "SECURITY")
    void eventsSuccessfulAuthTest() throws Exception {
        mockMvc.perform(get("/auth/events")).andExpectAll(
                status().isOk(),
                content().contentType("application/json")
        ).andDo(print());
    }

    @Test
    void eventsNoUserNotFoundTest() throws Exception {
        mockMvc.perform(get("/auth/events"))
                .andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @WithMockUser(roles = "TIME")
    void eventsNotSecurityRoleForbiddenTest() throws Exception {
        mockMvc.perform(get("/auth/events"))
                .andExpect(status().isForbidden()).andDo(print());
    }

}
