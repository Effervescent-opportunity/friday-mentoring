package com.friday.mentoring.service;

import com.friday.mentoring.dto.AuthEventDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthEventListenerTest {//todo why spy-mock is resetting between tests? & extract constants

    AuthEventService authEventService = Mockito.spy(Mockito.mock(AuthEventService.class));

    @InjectMocks
    AuthEventListener authEventListener;

    @Test
    public void detailsInstanceOfWebAuthenticationDetails() {
        Map<String, Object> eventData = Map.of("details", new WebAuthenticationDetails("remoteAddress", "sessionId"));
        AuditApplicationEvent event = new AuditApplicationEvent("user", "type", eventData);

        Mockito.doAnswer(invocation -> {
            if (invocation.getArgument(0) instanceof AuthEventDto authEventDto) {
                assertEquals("user", authEventDto.userName());
                assertEquals("type", authEventDto.type());
                assertEquals("remoteAddress", authEventDto.ipAddress());
                assertEquals(OffsetDateTime.ofInstant(event.getAuditEvent().getTimestamp(), ZoneId.systemDefault()), authEventDto.time());
            } else {
                Assertions.fail("AuthEventDto expected");
            }
            return null;
        }).when(authEventService).processEvent(any());

        authEventListener.on(event);

        verify(authEventService).processEvent(any(AuthEventDto.class));
    }

    @Test
    public void noDetails() {
        AuditApplicationEvent event = new AuditApplicationEvent("user", "type", "data");

        Mockito.doAnswer(invocation -> {
            if (invocation.getArgument(0) instanceof AuthEventDto authEventDto) {
                assertEquals("user", authEventDto.userName());
                assertEquals("type", authEventDto.type());
                assertEquals("Unknown", authEventDto.ipAddress());
                assertEquals(OffsetDateTime.ofInstant(event.getAuditEvent().getTimestamp(), ZoneId.systemDefault()), authEventDto.time());
            } else {
                Assertions.fail("AuthEventDto expected");
            }
            return null;
        }).when(authEventService).processEvent(any());

        authEventListener.on(event);

        verify(authEventService).processEvent(any(AuthEventDto.class));
    }

}