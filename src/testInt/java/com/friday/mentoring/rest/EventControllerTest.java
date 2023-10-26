package com.friday.mentoring.rest;

import com.friday.mentoring.BaseIntegrationTest;
import com.friday.mentoring.jpa.AuthEventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class EventControllerTest extends BaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AuthEventRepository authEventRepository;

    @AfterEach
    void tearDown() {
        authEventRepository.deleteAll();
    }

    @Sql("/add_events.sql")
    @Test
    void successFilterTest() throws Exception {//todo write tests?
        mockMvc.perform(get("/auth/events").param("userName", "root"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"),
                        jsonPath("numberOfElements").value(3)
                ).andDo(print());
    }

    @Test
    void incorrectPageNumberTest() throws Exception {
        mockMvc.perform(get("/auth/events").param("page", "-1"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith("application/problem+json"),
                        jsonPath("detail").value("Номер страницы должен быть неотрицательным")
                ).andDo(print());
    }
}
