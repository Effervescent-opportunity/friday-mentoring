package com.friday.mentoring.rest;

import com.friday.mentoring.jpa.AuthEventEntity;
import com.friday.mentoring.usecase.EventRepository;
import com.friday.mentoring.usecase.SiemEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AuthEventController {//todo maybe rename to EventController?

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEventController.class);

    private final EventRepository eventRepository;

    public AuthEventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping(path = "auth/events", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AuthEventEntity>> getEvents() {
        //todo use custom Page class Page
        //кто (логин, IP) что (тип события) и когда (дата и время) производил.
        //page, sort, filter
//        Sort
        return ResponseEntity.ok(Page.empty());
    }

    //todo maybe ip by mask? or how it's correct like 127.*.*.*
    //todo make indexes (word indices is correct but unused)
    //who ?user=root&?ip=127.0.0.5
    //what ?type=Auth_success
    //when ?dateFrom=2015-01-01T16:33:23&?dateTo=2016-03-07T14:55:32
    //page ?page=0?size=100500 (incorrect, max size = 100)
    //sort ?sort=column1,direction1&sort=column2,direction2
    public void aaaa(@RequestParam(name = "user", required = false) String user,
                     @RequestParam(name = "ip", required = false) String ip,
                     @RequestParam(name = "type", required = false) String type,
                     @RequestParam(name = "dateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateFrom,
                     @RequestParam(name = "dateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateTo,
                     @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                     @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                     @RequestParam(name = "sort", required = false, defaultValue = "id,desc") String[] sort) {
        //https://stackoverflow.com/questions/62676920/spring-boot-offsetdatetime-in-query-param
        //todo check date parsing, they can not work -> object mapper/ and try spring.mvc.format.date-format=yyyy-MM-dd'T'HH:mm:ss.SSSXXX
    }

    private record AuthEventDto(String ipAddress, OffsetDateTime time, String userName, SiemEventType eventType) {
    }
}
