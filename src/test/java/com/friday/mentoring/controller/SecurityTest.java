package com.friday.mentoring.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SecurityTest {//todo fix
    @Autowired
    MockMvc mockMvc;

//    static PostgreSQLContainer<?> setupPostgreSQLContainer() {
//        PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.4-alpine")
//                .withDatabaseName("fmdb")
//                .withUsername("fmuser")
//                .withPassword("fmpass");
//        postgreSQLContainer.withInitScript("initdb.sql");
//        return postgreSQLContainer;
//    }

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
    @WithMockUser(value = "noRoot")
    void clockNotRootUserForbiddenTest() throws Exception {
        mockMvc.perform(get("/time/current/utc"))
                .andExpect(status().isForbidden()).andDo(print());

        mockMvc.perform(get("/time/current").param("timezone", "Europe/Paris"))
                .andExpect(status().isForbidden()).andDo(print());
    }

}
