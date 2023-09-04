package com.friday.mentoring.controller;

import com.friday.mentoring.service.ClockService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ClockController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ClockControllerTest {//todo this is test? or integrationTest
    DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @MockBean
    ClockService clockService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void getNowInUtcTest() throws Exception {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        Mockito.when(clockService.getNowInUtc()).thenReturn(now);
        mockMvc.perform(get("/time/current/utc")).andExpectAll(
                status().isOk(),
                content().contentType("application/json"),
                jsonPath("timestamp").value(ISO_FORMATTER.format(now))
        );
    }

    @Test
    void getNowInTimezoneTest() throws Exception {
        String ianaTimezone = "Europe/Paris";
        ZoneId zoneId = ZoneId.of(ianaTimezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        Mockito.when(clockService.getNowInTimezone(ianaTimezone)).thenReturn(now);

        mockMvc.perform(get("/time/current").param("timezone", ianaTimezone)).andExpectAll(
                status().isOk(),
                content().contentType("application/json"),
                jsonPath("timestamp").value(ISO_FORMATTER.format(now))
        ).andDo(print());
    }

    @Test
    void getNowInIncorrectTimezoneTest() throws Exception {
        String tz = "Asia/Paris";
        String expectedDetail = "whatever";

        Mockito.when(clockService.getNowInTimezone(tz)).thenThrow(new DateTimeException(expectedDetail));

        mockMvc.perform(get("/time/current").param("timezone", tz))
                .andExpectAll(
                        status().isBadRequest(),
                        // Проверяем интеграционный контракт Problem Details
                        content().contentTypeCompatibleWith("application/problem+json"),
                        jsonPath("detail").value(expectedDetail)
                ).andDo(print());
    }

    @Test
    void testMissingRequiredParameter() throws Exception {
        mockMvc.perform(get("/time/current"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith("application/problem+json")
                ).andDo(print());
    }

}
