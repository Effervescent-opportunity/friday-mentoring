package com.friday.mentoring.security;

import com.friday.mentoring.todo.AuthEventType;
import com.friday.mentoring.usecase.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthEventListenerTest {

    static final String LOCAL_IP_ADDRESS = "127.0.0.1";
    static final String ROOT_USERNAME = "root";

    @Mock
    EventRepository eventRepository;
    @InjectMocks
    AuthEventListener authEventListener;

    @Test
    public void detailsInstanceOfWebAuthenticationDetailsTest() {
        Map<String, Object> eventData = Map.of("details", new WebAuthenticationDetails(LOCAL_IP_ADDRESS, "sessionId"));
        AuditApplicationEvent event = new AuditApplicationEvent(ROOT_USERNAME, AuthEventType.AUTHENTICATION_SUCCESS.name(), eventData);

        authEventListener.onAuditApplicationEvent(event);

        verify(eventRepository).save(LOCAL_IP_ADDRESS, OffsetDateTime.ofInstant(event.getAuditEvent().getTimestamp(), ZoneId.systemDefault()),
                ROOT_USERNAME, AuthEventType.AUTHENTICATION_SUCCESS);
    }

    @Test
    public void noDetailsTest() {
        AuditApplicationEvent event = new AuditApplicationEvent(ROOT_USERNAME, AuthEventType.AUTHENTICATION_FAILURE.name());

        authEventListener.onAuditApplicationEvent(event);

        verify(eventRepository).save("Unknown", OffsetDateTime.ofInstant(event.getAuditEvent().getTimestamp(), ZoneId.systemDefault()),
                ROOT_USERNAME, AuthEventType.AUTHENTICATION_FAILURE);
    }

}