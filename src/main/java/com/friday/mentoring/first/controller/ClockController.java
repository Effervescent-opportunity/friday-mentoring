package com.friday.mentoring.first.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClockController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClockController.class);

    //curl http://localhost:8080/time/current/utc
    @GetMapping(path = "/time/current/utc", produces = "application/json")//todo is it really json?
    public String getCurrentDateTimeInUtc() {
        LOGGER.info("LALALA I got request");
        return String.valueOf(System.currentTimeMillis());
    }

    //todo should I return String? or response entity...
    //http://localhost:8080/time/current?timezone=xxx
    //http://localhost:8080/time/current?timezone=Asia/Bangkok
    @GetMapping("/time/current")
    public String getCurrentDateTimeinTimezone(@RequestParam("timezone") String ianaTimezone) {
        //todo check if it is iana
        //todo t- what about "/" in iana format
        LOGGER.info("LALALA I got request with timezone {}", ianaTimezone);
        return String.valueOf(System.currentTimeMillis());
    }
}
