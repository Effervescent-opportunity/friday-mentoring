package com.friday.mentoring.service;

import com.friday.mentoring.event.AuthEventType;
import com.friday.mentoring.event.repository.AuthEventSaver;
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

    private final AuthEventSaver authEventSaver;

    public AuthEventListener(AuthEventSaver authEventSaver) {
        this.authEventSaver = authEventSaver;
    }

    @EventListener
    public void on(AuditApplicationEvent event) {
        AuditEvent auditEvent = event.getAuditEvent();

        String ipAddress = "Unknown";
        if (auditEvent.getData().get("details") instanceof WebAuthenticationDetails details) {
            ipAddress = details.getRemoteAddress();
        }

        authEventSaver.save(ipAddress, OffsetDateTime.ofInstant(auditEvent.getTimestamp(), ZoneId.systemDefault()),
                auditEvent.getPrincipal(), AuthEventType.valueOf(auditEvent.getType()));
    }

}
