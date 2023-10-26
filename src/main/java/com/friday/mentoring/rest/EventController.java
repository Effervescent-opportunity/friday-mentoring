package com.friday.mentoring.rest;

import com.friday.mentoring.jpa.AuthEventEntity;
import com.friday.mentoring.usecase.EventRepository;
import org.springframework.data.domain.Page;
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
                                                        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                        @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                        @RequestParam(name = "sort", required = false, defaultValue = "id,desc") String[] sort) {
        Page<AuthEventEntity> filteredEntities = eventRepository.getFilteredEntities(userName, ipAddress, eventType, timeFrom, timeTo, page, size, sort);

        return ResponseEntity.ok(filteredEntities
                .map(entity -> new AuthEventDto(entity.getIpAddress(), entity.getEventTime(), entity.getUserName(), entity.getEventType())));
    }

    private record AuthEventDto(String ipAddress, OffsetDateTime time, String userName, String eventType) {
    }
}
