package com.friday.mentoring.controller;

import com.friday.mentoring.service.ClockService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);


    @PostMapping(path = "auth/login")
    public ResponseEntity login(String user, String password, HttpServletRequest httpServletRequest) {
        LOGGER.info("got user [{}], password [{}]", user, password);
        try {//todo
            httpServletRequest.login(user, password);
            return ResponseEntity.ok().build();
        } catch (ServletException ex) {
            LOGGER.info("got servlet exception ", ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping(path = "auth/login1")
    public ResponseEntity login1(@RequestParam String user, @RequestParam String password, HttpServletRequest httpServletRequest) {
        LOGGER.info("got user [{}], password [{}]", user, password);
        try {//todo
            httpServletRequest.login(user, password);
            return ResponseEntity.ok().build();
        } catch (ServletException ex) {
            LOGGER.info("got servlet exception ", ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping(path = "auth/logout")
    public ResponseEntity logout(HttpServletRequest httpServletRequest) {
        try {
            httpServletRequest.logout();
            return ResponseEntity.ok().build();
        } catch (ServletException ex) {
            LOGGER.info("got servlet exception ", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
