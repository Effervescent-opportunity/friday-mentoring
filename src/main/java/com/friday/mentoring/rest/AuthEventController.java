package com.friday.mentoring.rest;

import com.friday.mentoring.jpa.AuthEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AuthEventController {//todo maybe rename to EventController?

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEventController.class);

    @GetMapping(path = "auth/event", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuthEventEntity>> getEvents() {
        //todo use custom Page class Page
        //кто (логин, IP) что (тип события) и когда (дата и время) производил.
        //page, sort, filter
//        Sort
        return ResponseEntity.ok(new ArrayList<>());
    }

    //todo maybe ip by mask? or how it's correct like 127.*.*.*
    //todo make indexes (word indices is correct by unused)
    //who ?user=root&?ip=127.0.0.5
    //what ?type=Auth_success
    //when ?dateFrom=2015-01-01T16:33:23&?dateTo=2016-03-07T14:55:32
    //page ?page=0?size=100500 (incorrect, max size = 100)
    //sort ?sort=column1,direction1&sort=column2,direction2
    public void aaaa(      @RequestParam(required = false) String title,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "id,desc") String[] sort) {
        //
    }
}
