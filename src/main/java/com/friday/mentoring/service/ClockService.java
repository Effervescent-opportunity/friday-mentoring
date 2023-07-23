package com.friday.mentoring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Получение текущей даты и времени в разных таймзонах
 */
@Service
public class ClockService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClockService.class);

    @PreAuthorize("hasRole('ROLE_ADMIN')") //todo what's the difference between this and secured?
    public ZonedDateTime getNowInUtc() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }

    @Secured("ROLE_ADMIN")
    public ZonedDateTime getNowInTimezone(String ianaTimezone) {
        try {
            ZoneId zoneId = ZoneId.of(ianaTimezone);
            return ZonedDateTime.now(zoneId);
        } catch (Exception ex) {
            LOGGER.warn("Invalid timezone [{}]", ianaTimezone, ex);
            throw ex;
        }
    }

}
