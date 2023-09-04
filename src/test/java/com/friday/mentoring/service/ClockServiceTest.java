package com.friday.mentoring.service;

import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.zone.ZoneRulesException;

import static org.junit.jupiter.api.Assertions.*;

class ClockServiceTest {//todo this is test
    private final ClockService clockService = new ClockService();

    @Test
    public void getNowInUtcTest() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        ZonedDateTime dateFromService = clockService.getNowInUtc();

        assertTrue(now.isBefore(dateFromService));
        assertEquals(now.getOffset(), dateFromService.getOffset());
        assertTrue(ZonedDateTime.now(ZoneOffset.UTC).isAfter(dateFromService));
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

        ZonedDateTime dateFromService = clockService.getNowInTimezone(ianaTimezone);

        assertTrue(now.isBefore(dateFromService));
        assertEquals(now.getOffset(), dateFromService.getOffset());
        assertTrue(ZonedDateTime.now(zoneId).isAfter(dateFromService));
    }
}