package com.friday.mentoring.rest;

import com.friday.mentoring.BaseIntegrationTest;
import com.friday.mentoring.jpa.AuthEventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

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
    void emptyFilterTest() throws Exception {
        mockMvc.perform(get("/auth/events"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"),
                        jsonPath("numberOfElements").value(5)
                ).andDo(print());
    }

    @Sql("/add_events.sql")
    @Test
    void successFilterTest() throws Exception {
        mockMvc.perform(get("/auth/events").param("userName", "root")
                        .param("ipAddress", "127.0.0.1")
                        .param("sort", "eventTime, asc")
                        .param("size", "1"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType("application/json"),
                        jsonPath("numberOfElements").value(1),
                        jsonPath("totalPages").value(3),
                        jsonPath("content[0].eventType").value("AUTHENTICATION_SUCCESS")
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

    @Test
    void incorrectSizeTest() throws Exception {
        mockMvc.perform(get("/auth/events").param("size", "0"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith("application/problem+json"),
                        jsonPath("detail").value("Допустимый размер страницы: от 1 до 100 включительно")
                ).andDo(print());

        mockMvc.perform(get("/auth/events").param("size", "101"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith("application/problem+json"),
                        jsonPath("detail").value("Допустимый размер страницы: от 1 до 100 включительно")
                ).andDo(print());
    }

    @Test
    void incorrectTimeIntervalTest() throws Exception {
        mockMvc.perform(get("/auth/events").param("timeFrom", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("timeTo", OffsetDateTime.now().minusHours(1).format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith("application/problem+json"),
                        jsonPath("detail").value("Дата начала периода должна быть до даты окончания периода")
                ).andDo(print());
    }

    @Test
    void incorrectSortTest() throws Exception {
        mockMvc.perform(get("/auth/events").param("sort", "incorrect"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith("application/problem+json"),
                        jsonPath("detail").value("Сортировка задана некорректно")
                ).andDo(print());

        mockMvc.perform(get("/auth/events").param("sort", "user,asc"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith("application/problem+json"),
                        jsonPath("detail").value("Некорректное название поля для сортировки: user")
                ).andDo(print());

        mockMvc.perform(get("/auth/events").param("sort", "userName,upper"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith("application/problem+json"),
                        jsonPath("detail").value("Некорректное направление сортировки: upper")
                ).andDo(print());
    }
}
