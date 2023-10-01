package com.friday.mentoring.security;

import com.friday.mentoring.todo.AuthEventType;
import com.friday.mentoring.usecase.EventRepository;
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

        eventRepository.save(ipAddress, OffsetDateTime.ofInstant(auditEvent.getTimestamp(), ZoneId.systemDefault()),
                auditEvent.getPrincipal(), AuthEventType.valueOf(auditEvent.getType()));
    }

}
