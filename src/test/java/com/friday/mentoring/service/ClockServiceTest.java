package com.friday.mentoring.service;

import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRulesException;

import static org.junit.jupiter.api.Assertions.*;

class ClockServiceTest {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private final ClockService clockService = new ClockService();

    @Test
    public void getNowInUtcTest() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        ZonedDateTime dateFromService = ZonedDateTime.parse(clockService.getNowInUtc(), FORMATTER);

        assertTrue(now.isBefore(dateFromService));
        assertEquals(now.getOffset(), dateFromService.getOffset());
        assertTrue(ZonedDateTime.now(ZoneOffset.UTC).isAfter(dateFromService));
//todo del
//        System.out.println(now);
//        System.out.println(dateFromService);
//        System.out.println(ZonedDateTime.now(ZoneOffset.UTC));
    }

    @Test
    public void getNowInNullTimezoneTest() {
        assertThrows(NullPointerException.class, () -> clockService.getNowInTimezone(null));
    }

    @Test
    public void getNowInEmptyTimezoneTest() {
        assertThrows(DateTimeException.class, () -> clockService.getNowInTimezone(""));
    }

    @Test
    public void getNowInIncorrectTimezoneTest() {
        assertThrows(ZoneRulesException.class, () -> clockService.getNowInTimezone("Asia/London"));
    }

    @Test
    public void getNowInAntarcticaTest() {
        getNowInTimezoneTest("Antarctica/Macquarie");
    }

    @Test
    public void getNowInLondonTest() {
        getNowInTimezoneTest("Europe/London");
    }

    @Test
    public void getNowInKamchatkaTest() {
        getNowInTimezoneTest("Asia/Kamchatka");
    }

    private void getNowInTimezoneTest(String ianaTimezone) {
        ZoneId zoneId = ZoneId.of(ianaTimezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        ZonedDateTime dateFromService = ZonedDateTime.parse(clockService.getNowInTimezone(ianaTimezone), FORMATTER);

        assertTrue(now.isBefore(dateFromService));
        assertEquals(now.getOffset(), dateFromService.getOffset());
        assertTrue(ZonedDateTime.now(zoneId).isAfter(dateFromService));
    }
}