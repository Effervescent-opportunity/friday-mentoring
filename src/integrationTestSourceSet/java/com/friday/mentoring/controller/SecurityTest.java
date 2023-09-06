package com.friday.mentoring.controller;

import com.friday.mentoring.BaseIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
public class SecurityTest extends BaseIntegrationTest {//todo this is integrationTest
    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        TestSecurityContextHolder.clearContext();
    }

    @Test
    void successfulAuthTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"root\", \"password\": \"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    void unSuccessfulAuthTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user\": \"noRoot\", \"password\": \"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser(value = "root", roles = "ADMIN")
    void clockSuccessfulAuthTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/time/current/utc")).andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.content().contentType("application/json")
        ).andDo(MockMvcResultHandlers.print());

        mockMvc.perform(MockMvcRequestBuilders.get("/time/current").param("timezone", "Europe/Paris"))
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.content().contentType("application/json")
                ).andDo(MockMvcResultHandlers.print());
    }

    @Test
    void clockNoUserNotFoundTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/time/current/utc"))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andDo(MockMvcResultHandlers.print());

        mockMvc.perform(MockMvcRequestBuilders.get("/time/current").param("timezone", "Europe/Paris"))
                .andExpect(MockMvcResultMatchers.status().isNotFound()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser(value = "noRoot")
    void clockNotRootUserForbiddenTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/time/current/utc"))
                .andExpect(MockMvcResultMatchers.status().isForbidden()).andDo(MockMvcResultHandlers.print());

        mockMvc.perform(MockMvcRequestBuilders.get("/time/current").param("timezone", "Europe/Paris"))
                .andExpect(MockMvcResultMatchers.status().isForbidden()).andDo(MockMvcResultHandlers.print());
    }

}
