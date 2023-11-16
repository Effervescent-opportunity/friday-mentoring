package com.friday.mentoring.rest;

import com.friday.mentoring.BaseIntegrationTest;
import com.friday.mentoring.jpa.AuthEventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DirtiesContext
@AutoConfigureMockMvc
@WithMockUser(roles = "SECURITY")
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
    void emptyFilterTest() throws Exception {
        mockMvc.perform(get("/auth/events"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"),
                        jsonPath("numberOfElements").value(5),
                        jsonPath("size").value(20),
                        jsonPath("sort.sorted").value(true)
                ).andDo(print());
    }

    @Sql("/add_events.sql")
    @Test
    void successFiltersTest() throws Exception {
        mockMvc.perform(get("/auth/events").param("userName", "root")
                        .param("timeTo", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("ipAddress", "127.0.0.1")
                        .param("sort", "eventTime,asc")
                        .param("size", "1"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"),
                        jsonPath("numberOfElements").value(1),
                        jsonPath("totalPages").value(3),
                        jsonPath("content[0].eventType").value("AUTHENTICATION_SUCCESS"),
                        jsonPath("content[0].userName").value("root")
                ).andDo(print());

        mockMvc.perform(get("/auth/events").param("eventType", "AUTHENTICATION_SUCCESS")
                        .param("timeFrom", OffsetDateTime.of(2022, 1, 1, 1, 1, 1, 0, ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("timeTo", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("sort", "userName,desc")
                        .param("sort", "eventTime,asc")
                        .param("page", "1")
                        .param("size", "1"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"),
                        jsonPath("numberOfElements").value(1),
                        jsonPath("totalPages").value(3),
                        jsonPath("number").value(1),
                        jsonPath("content[0].eventType").value("AUTHENTICATION_SUCCESS"),
                        jsonPath("content[0].userName").value("other")
                ).andDo(print());
    }

    @Sql("/add_events.sql")
    @Test
    void incorrectPageNumberTest() throws Exception {
        mockMvc.perform(get("/auth/events").param("page", "-1"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"),
                        jsonPath("numberOfElements").value(5),
                        jsonPath("size").value(20),
                        jsonPath("number").value(0)
                ).andDo(print());
    }

    @Sql("/add_events.sql")
    @Test
    void incorrectSizeTest() throws Exception {
        mockMvc.perform(get("/auth/events").param("size", "0"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"),
                        jsonPath("numberOfElements").value(5),
                        jsonPath("size").value(20),
                        jsonPath("number").value(0)
                ).andDo(print());

        mockMvc.perform(get("/auth/events").param("size", "101"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"),
                        jsonPath("numberOfElements").value(5),
                        jsonPath("size").value(100),
                        jsonPath("number").value(0)
                ).andDo(print());
    }

    @Test
    void incorrectTimeIntervalTest() throws Exception {
        mockMvc.perform(get("/auth/events").param("timeFrom", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("timeTo", OffsetDateTime.now().minusHours(1).format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith("application/problem+json"),
                        jsonPath("detail").value("EventTimeFrom must be less than eventTimeTo")
                ).andDo(print());
    }

    @Test
    void incorrectSortTest() throws Exception {
        mockMvc.perform(get("/auth/events").param("sort", "incorrect"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith("application/problem+json"),
                        jsonPath("detail").value("Incorrect property name for sorting: incorrect")
                ).andDo(print());
    }
}
