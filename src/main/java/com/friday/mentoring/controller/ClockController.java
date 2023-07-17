package com.friday.mentoring.controller;

import com.friday.mentoring.service.ClockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

/*
1. produces = MediaType.APPLICATION_JSON_VALUE - лишнее, без него отлично работает;
 */
@RestController
public class ClockController {

    private final ClockService clockService;

    public ClockController(ClockService clockService) {
        this.clockService = clockService;
    }

    /**
     * Получение текущей даты и текущего времени в таймзоне UTC
     */
    @GetMapping(path = "/time/current/utc")
    public CurrentTimeResponse getCurrentDateTimeInUtc() {
        var nowInUtc = clockService.getNowInUtc();
        return new CurrentTimeResponse(nowInUtc);
    }

    /**
     * Получение текущей даты и текущего времени в заданной таймзоне (в запросе задаётся форматом IANA `Area/Location`).
     */
    @GetMapping(path = "/time/current")
    public CurrentTimeResponse getCurrentDateTimeInTimezone(@RequestParam("timezone") String ianaTimezone) {
        var nowInTimezone = clockService.getNowInTimezone(ianaTimezone);
        return new CurrentTimeResponse(nowInTimezone);
    }

    /*
       2. возврат строки как JSON - ужасная практика (да, технически это валидный JSON, однако для REST API
          нужно использовать реализацию, совместимую с RFC 4627 JSON-text = object / array, а закидоны RFC 7159/8259
          JSON-text = ws value ws использовать не нужно по причинам:
             а) несовместимости с некоторыми клиентами;
             б) непонимания средним человеком-читателем.
     */
    record CurrentTimeResponse(ZonedDateTime timestamp) {

    }

}
