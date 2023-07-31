package com.friday.mentoring.controller;

import com.friday.mentoring.service.KafkaProducer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для аутентификации
 */
@RestController
public class AuthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final KafkaProducer kafkaProducer;

    public AuthController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping(path = "auth/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> login(@RequestBody Credentials credentials, HttpServletRequest httpServletRequest) {
        try {
            httpServletRequest.login(credentials.user(), credentials.password());
//            kafkaProducer.sendMessage("SUCCESS login");
            return ResponseEntity.ok().build();
        } catch (ServletException ex) {
            LOGGER.info("Got exception while logging in with user [{}]", credentials.user(), ex);
//            kafkaProducer.sendMessage("FAIL login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    record Credentials(String user, String password) {

    }
}
