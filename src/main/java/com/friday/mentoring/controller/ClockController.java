package com.friday.mentoring.controller;

import com.friday.mentoring.service.ClockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@RestController
public class ClockController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClockController.class);

    private final ClockService clockService;

    public ClockController(ClockService clockService) {
        this.clockService = clockService;
    }

    //curl http://localhost:8080/time/current/utc
    //"2023-07-08T17:29:17.496701084Z" - not iso 8601
    @GetMapping(path = "/time/current/utc", produces = MediaType.APPLICATION_JSON_VALUE)//todo is it really json?
    public ResponseEntity<String> getCurrentDateTimeInUtc() {
        LOGGER.info("LALALA I got request");
        return ResponseEntity.ok(clockService.getNowInUtc());
        //todo wtf SyntaxError: JSON.parse: unexpected non-whitespace character after JSON data at line 1 column 5 of the JSON data
    }

    @GetMapping(path = "/time/current/utc1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ZonedDateTime> getCurrentDateTimeInUtc1() {
        LOGGER.info("LALALA I got request1");
        return ResponseEntity.ok(ZonedDateTime.now(ZoneOffset.UTC));//only this is real json (with ")
    }


    //todo should I return String? or response entity...
    //http://localhost:8080/time/current?timezone=xxx
    //http://localhost:8080/time/current?timezone=Asia/Bangkok
    //todo curl http://localhost:8080/time/current?timezone=Europe/Moscow&la=lalqa not working from terminal

    //"2023-07-09T00:30:10.572164887+07:00"
    @GetMapping(path = "/time/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCurrentDateTimeinTimezone(@RequestParam("timezone") String ianaTimezone) {
        try {
            return ResponseEntity.ok(clockService.getNowInTimezone(ianaTimezone));
        } catch (Exception ex) {
            LOGGER.warn("Timezone [{}] is incorrect: {}", ianaTimezone, ex.getMessage());
            return ResponseEntity.badRequest().body("Incorrect timezone: " + ianaTimezone);//maybe not in body? exception handling
        }

        //todo check if it is iana - IllegalArgumentException
        //java.time.zone.ZoneRulesException: Unknown time-zone ID: Asia/Banllgkok
        //java.time.DateTimeException: Invalid ID for ZoneOffset, invalid format:
        //todo t- what about "/" in iana format
//        LOGGER.info("LALALA I got request with timezone [{}], la [{}]", ianaTimezone, la);
//
//        try {
//            ZoneId zoneId = ZoneId.of(ianaTimezone);
//            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
//            LOGGER.info("lalala got zonedDateTime {}", zonedDateTime);
//            return ResponseEntity.ok(zonedDateTime);
//        } catch (Exception ex) {
//            LOGGER.warn("Lalala exception in zoning {}", ianaTimezone, ex);
//        }
//        return ResponseEntity.ok(String.valueOf(System.currentTimeMillis()));
    }

    @GetMapping(path = "/time/current1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCurrentDateTimeinTimezone1(@RequestParam("timezone") String ianaTimezone) {//json for date, fail json for string
        try {
            ZoneId zoneId = ZoneId.of(ianaTimezone);
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

            return ResponseEntity.ok(zonedDateTime);
        } catch (Exception ex) {
            LOGGER.warn("Timezone [{}] is incorrect: {}", ianaTimezone, ex.getMessage());
            return ResponseEntity.badRequest().body("Incorrect timezone: " + ianaTimezone);
        }
    }

    @GetMapping(path = "/time/current2", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCurrentDateTimeinTimezone2(@RequestParam("timezone") String ianaTimezone) {//fail json for both
        try {
            ZoneId zoneId = ZoneId.of(ianaTimezone);
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

            return ResponseEntity.ok(clockService.getNowInTimezone(ianaTimezone));
        } catch (Exception ex) {
            LOGGER.warn("Timezone [{}] is incorrect: {}", ianaTimezone, ex.getMessage());
            return ResponseEntity.badRequest().body("Incorrect timezone: " + ianaTimezone);
        }
    }

    @GetMapping(path = "/time/current3", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ZonedDateTime> getCurrentDateTimeinTimezone3(@RequestParam("timezone") String ianaTimezone) {//

        ZoneId zoneId = ZoneId.of(ianaTimezone);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

        return ResponseEntity.ok(zonedDateTime);

    }
}
