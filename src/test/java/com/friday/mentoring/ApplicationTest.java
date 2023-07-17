package com.friday.mentoring;

import com.friday.mentoring.controller.ClockController;
import com.friday.mentoring.service.ClockService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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

/*
Т.к. сервисный слой уже проверен, здесь можно:

1. использовать меньший объём контекста и поднимать только web часть
2. проверять, что контроллер отдаёт то, что получает от сервиса - без самодеятельности и модификаций

Разделение тестов на "сервисный" и "web" слой максимально удачно, т.к. на Web слое нельзя тривиальным образом
проверить те граничные случаи, которые легко проверяются тестом сервиса.

Конкретно этот тест нужно перенести в пакет controller и переименовать в ClockControllerTest.

Заморачиваться с финальностью и областями видимости в тестах - нет смысла.
Хотя это - дело вкуса и обычно не закрепляется ни в каких правилах.
 */
@WebMvcTest(controllers = ClockController.class)
class ApplicationTest {
    // !!! Очень изящная реализация теста через ISO DateTimeFormatter, она позволяет не заморачиваться
    // с хаками наподобие isAfter/isBefore для времени, но мысль не была доведена до конца :(
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
        var tz = "Asia/Paris";
        var expectedDetail = "whatever";

        Mockito.when(clockService.getNowInTimezone(tz)).thenThrow(new DateTimeException(expectedDetail));

        mockMvc.perform(get("/time/current").param("timezone", tz))
                .andExpectAll(
                        status().isBadRequest(),
                        // Проверяем интеграционный контракт Problem Details
                        content().contentTypeCompatibleWith("application/problem+json"),
                        jsonPath("detail").value(expectedDetail)
                ).andDo(print());
    }

    // Необходим тест реакции системы на пропущенный обязательный параметр timezone,
    // раз в контроллере он (неявно) объявлен как required
    @Test
    void testMissingRequiredParameter() throws Exception {
        mockMvc.perform(get("/time/current"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith("application/problem+json")
                ).andDo(print());
    }

}
