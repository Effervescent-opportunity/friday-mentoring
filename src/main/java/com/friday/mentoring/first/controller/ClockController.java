package com.friday.mentoring.first.controller;

import com.friday.mentoring.first.service.ClockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

@RestController
public class ClockController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClockController.class);

    private final ClockService clockService;

    public ClockController(ClockService clockService) {
        this.clockService = clockService;
    }

    //curl http://localhost:8080/time/current/utc
    //"2023-07-08T17:29:17.496701084Z" - not iso 8601
    @GetMapping(path = "/time/current/utc", produces = "application/json")//todo is it really json?
    public ResponseEntity<String> getCurrentDateTimeInUtc() {
        LOGGER.info("LALALA I got request");
        return ResponseEntity.ok(clockService.getNowInUtc());
        //todo wtf SyntaxError: JSON.parse: unexpected non-whitespace character after JSON data at line 1 column 5 of the JSON data
    }

    //todo should I return String? or response entity...
    //http://localhost:8080/time/current?timezone=xxx
    //http://localhost:8080/time/current?timezone=Asia/Bangkok

    //"2023-07-09T00:30:10.572164887+07:00"
    @GetMapping("/time/current")
    public ResponseEntity getCurrentDateTimeinTimezone(@RequestParam("timezone") String ianaTimezone, @RequestParam("la") String la) {
        //todo check if it is iana - IllegalArgumentException
        //java.time.zone.ZoneRulesException: Unknown time-zone ID: Asia/Banllgkok
        //java.time.DateTimeException: Invalid ID for ZoneOffset, invalid format:
        //todo t- what about "/" in iana format
        LOGGER.info("LALALA I got request with timezone [{}], la [{}]", ianaTimezone, la);

        try {
            ZoneId zoneId = ZoneId.of(ianaTimezone);
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
            LOGGER.info("lalala got zonedDateTime {}", zonedDateTime);
            return ResponseEntity.ok(zonedDateTime);
        } catch (Exception ex) {
            LOGGER.warn("Lalala exception in zoning {}", ianaTimezone, ex);
        }
        return ResponseEntity.ok(String.valueOf(System.currentTimeMillis()));
    }
}
