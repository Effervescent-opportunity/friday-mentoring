package com.friday.mentoring.first.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ClockService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClockService.class);
    //ISO 8601 с таймзоной;:
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public String getNowInUtc() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        LOGGER.info("aaaaaaaaaaaaaa {}", FORMATTER.format(zonedDateTime));
        return FORMATTER.format(zonedDateTime);
        //todo it's still 2023-07-08T17:54:08.053172659Z
    }

    public String getNowInTimezone(String ianaTimezone) {
        try {
            ZoneId zoneId = ZoneId.of(ianaTimezone);
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
            LOGGER.info("lalala got zonedDateTime {}", zonedDateTime);
            return FORMATTER.format(zonedDateTime);
        } catch (Exception ex) {
            LOGGER.warn("Lalala exception in zoning {}", ianaTimezone, ex);
        }
return null;
    }

}
