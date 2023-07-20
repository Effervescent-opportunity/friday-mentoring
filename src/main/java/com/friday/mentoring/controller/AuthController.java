package com.friday.mentoring.controller;

import com.friday.mentoring.service.ClockService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);


    //header: Content-Type: application/json
    //body: {"user": "root", "password": "password"}

    //then add header Cookie: JSESSIONID=F8B3D24977A0F1342CC890528E34908C to clock controller requests and logout requests
    // cookie was sent in response header for login
    //ааааа при этом я могу 3 раза залогиниться, получить 3 разных JSESSIONID, разлогиниться из одного, и другие останутся рабочими

    @PostMapping(path = "auth/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody Credentials credentials, HttpServletRequest httpServletRequest) {
        LOGGER.info("got credentials [{}]", credentials);
        try {//todo
            httpServletRequest.login(credentials.user(), credentials.password());
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

    record Credentials(String user, String password) {

    }
}
