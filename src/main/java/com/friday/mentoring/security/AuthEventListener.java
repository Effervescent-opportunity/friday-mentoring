package com.friday.mentoring.security;

import com.friday.mentoring.usecase.AuthEventType;
import com.friday.mentoring.usecase.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * Слушает события аудита (только включенные)
 */
@Component
public class AuthEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEventListener.class);

    private final EventRepository eventRepository;

    public AuthEventListener(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @EventListener
    public void onAuditApplicationEvent(AuditApplicationEvent event) {
        AuditEvent auditEvent = event.getAuditEvent();

        String ipAddress = "Unknown";
        if (auditEvent.getData().get("details") instanceof WebAuthenticationDetails details) {
            ipAddress = details.getRemoteAddress();
        }

        AuthEventType eventType = AuthEventType.getBySpringName(auditEvent.getType());
        if (eventType == null) {
            LOGGER.warn("Got AuditApplicationEvent with type [{}] not convertible to AuthEventType. Event won't be saved", auditEvent.getType());
            return;
        }

        eventRepository.save(ipAddress, OffsetDateTime.ofInstant(auditEvent.getTimestamp(), ZoneId.systemDefault()),
                auditEvent.getPrincipal(), eventType.name());
    }

}
