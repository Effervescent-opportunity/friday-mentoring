package com.friday.mentoring.rest;

import com.friday.mentoring.jpa.AuthEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AuthEventController {//todo maybe rename to EventController?

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEventController.class);

    @GetMapping(path = "auth/event", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuthEventEntity>> getEvents() {
        return ResponseEntity.ok(new ArrayList<>());
    }
}
