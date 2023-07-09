package com.friday.mentoring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@Service
public class ClockService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClockService.class);
    //ISO 8601 с таймзоной, хотя 9 символов после точки я бы убрала (date.truncatedTo(ChronoUnit.MILLIS) -> .123+xxx
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public String getNowInUtc() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        LOGGER.info("aaaaaaaaaaaaaa {}", FORMATTER.format(zonedDateTime));
        return FORMATTER.format(zonedDateTime);
    }

    public String getNowInTimezone(String ianaTimezone) {
        ZoneId zoneId = ZoneId.of(ianaTimezone);
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        LOGGER.info("lalala got zonedDateTime {}", FORMATTER.format(zonedDateTime));
        return FORMATTER.format(zonedDateTime);

        //          LOGGER.warn("Lalala exception in zoning {}", ianaTimezone, ex);
    }



}
