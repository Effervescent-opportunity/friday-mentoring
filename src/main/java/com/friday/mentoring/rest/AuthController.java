package com.friday.mentoring.rest;

import com.friday.mentoring.usecase.AuthEventType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для аутентификации
 */
@RestController
public class AuthController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(path = "auth/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> login(@RequestBody Credentials credentials, HttpServletRequest httpServletRequest) {
        try {
            httpServletRequest.login(credentials.user(), credentials.password());
            return ResponseEntity.ok().build();
            //todo на успех обнулить счетчик неуспеха
        } catch (ServletException ex) {
            LOGGER.info("Got exception while logging in with user [{}]", credentials.user(), ex);
            //todo на неуспех увеличить счетчик неуспеха + заблокировать если надо + сделать свой user details?

//            userRepository.processtLoginAttempts(userName);

            

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping(path = "auth/change-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRecord credentials, HttpServletRequest httpServletRequest) {
        String authenticatedUser = httpServletRequest.getRemoteUser();
        if (authenticatedUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (!authenticatedUser.equals(credentials.user)) {
            LOGGER.info("User [{}] tried to change user's [{}] password. Forbidden", authenticatedUser, credentials.user);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else if (!passwordEncoder.matches())

        userDetailsManager.changePassword(credentials.oldPassword(), credentials.newPassword());
        return ResponseEntity.ok().build();
    }

    record Credentials(String user, String password) {

    }

    record ChangePasswordRecord(String user, String oldPassword, String newPassword) {

    }

}
