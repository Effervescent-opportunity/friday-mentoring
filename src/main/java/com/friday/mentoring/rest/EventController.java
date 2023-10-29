package com.friday.mentoring.rest;

import com.friday.mentoring.usecase.EventRepository;
import com.friday.mentoring.usecase.EventRepository.AuthEventDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

/**
 * Контроллер для получения событий аутентификации и авторизации
 */
@RestController
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping(path = "auth/events", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AuthEventDto>> getEvents(@RequestParam(name = "userName", required = false) String userName,
                                                        @RequestParam(name = "ipAddress", required = false) String ipAddress,
                                                        @RequestParam(name = "eventType", required = false) String eventType,
                                                        @RequestParam(name = "timeFrom", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime timeFrom,
                                                        @RequestParam(name = "timeTo", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime timeTo,
                                                        Pageable pageable) {
        return ResponseEntity.ok(eventRepository.getFilteredEntities(userName, ipAddress, eventType, timeFrom, timeTo, pageable));
    }

}
