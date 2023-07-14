package com.friday.mentoring.controller;

import com.friday.mentoring.service.ClockService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    @GetMapping(path = "/time/current/utc", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ZonedDateTime> getCurrentDateTimeInUtc() {
        return ResponseEntity.ok(clockService.getNowInUtc());
    }

    /**
     * Получение текущей даты и текущего времени в заданной таймзоне (в запросе задаётся форматом IANA `Area/Location`).
     */
    @GetMapping(path = "/time/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ZonedDateTime> getCurrentDateTimeInTimezone(@RequestParam("timezone") String ianaTimezone) {
        return ResponseEntity.ok(clockService.getNowInTimezone(ianaTimezone));
    }

}
