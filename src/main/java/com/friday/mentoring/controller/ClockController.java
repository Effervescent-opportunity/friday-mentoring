package com.friday.mentoring.controller;

import com.friday.mentoring.service.ClockService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

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
        ZonedDateTime nowInUtc = clockService.getNowInUtc();
        return new CurrentTimeResponse(nowInUtc);
    }

    @GetMapping(path = "/time/current/utc1")
    public CurrentTimeResponse getCurrentDateTimeInUtc1(HttpServletRequest request) {
        SecurityContext context = SecurityContextHolder.getContext();
        ZonedDateTime nowInUtc = ZonedDateTime.now();
        return new CurrentTimeResponse(nowInUtc);
    }


    /**
     * Получение текущей даты и текущего времени в заданной таймзоне (в запросе задаётся форматом IANA `Area/Location`).
     */
    @GetMapping(path = "/time/current")
    public CurrentTimeResponse getCurrentDateTimeInTimezone(@RequestParam("timezone") String ianaTimezone) {
        ZonedDateTime nowInTimezone = clockService.getNowInTimezone(ianaTimezone);
        return new CurrentTimeResponse(nowInTimezone);
    }

    record CurrentTimeResponse(ZonedDateTime timestamp) {

    }

}
