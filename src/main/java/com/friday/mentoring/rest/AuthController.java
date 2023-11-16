package com.friday.mentoring.rest;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    public AuthController(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    @PostMapping(path = "auth/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> login(@RequestBody Credentials credentials, HttpServletRequest httpServletRequest) {
        try {
            httpServletRequest.login(credentials.user(), credentials.password());
            return ResponseEntity.ok().build();
        } catch (ServletException ex) {
            LOGGER.info("Got exception while logging in with user [{}]", credentials.user(), ex);
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
            //todo get real user name and trychange with it
        }

        userDetailsManager.changePassword(credentials.oldPassword(), credentials.newPassword());
        return ResponseEntity.ok().build();
    }

    /*
    2023-11-16 20:28:59.929 [https-jsse-nio-127.0.0.1-8443-exec-4] INFO  c.f.mentoring.rest.AuthController
    - lalala remoteUser [beza], userPrincipal [UsernamePasswordAuthenticationToken
    [Principal=org.springframework.security.core.userdetails.User [Username=beza, Password=[PROTECTED], Enabled=true,
    AccountNonExpired=true, credentialsNonExpired=true, AccountNonLocked=true, Granted Authorities=[ROLE_SECURITY]],
    Credentials=[PROTECTED], Authenticated=true, Details=WebAuthenticationDetails [RemoteIpAddress=127.0.0.1, SessionId=null],
     Granted Authorities=[ROLE_SECURITY]]]

     2023-11-16 20:32:01.508 [https-jsse-nio-127.0.0.1-8443-exec-1] INFO  c.f.mentoring.rest.AuthController - lalala
     remoteUser [root], userPrincipal [UsernamePasswordAuthenticationToken [Principal=org.springframework.security.core.userdetails.User
     [Username=root, Password=[PROTECTED], Enabled=true, AccountNonExpired=true, credentialsNonExpired=true, AccountNonLocked=true,
     Granted Authorities=[ROLE_ADMIN, ROLE_SECURITY, ROLE_TIME]], Credentials=[PROTECTED], Authenticated=true,
     Details=WebAuthenticationDetails
     [RemoteIpAddress=127.0.0.1, SessionId=null], Granted Authorities=[ROLE_ADMIN, ROLE_SECURITY, ROLE_TIME]]]
     */
    record Credentials(String user, String password) {

    }

    record ChangePasswordRecord(String user, String oldPassword, String newPassword) {

    }
}
