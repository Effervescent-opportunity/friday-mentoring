package com.friday.mentoring.service;

import com.friday.mentoring.dto.AuthEventDto;
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

    private final AuthEventService authEventService;

    public AuthEventListener(AuthEventService authEventService) {
        this.authEventService = authEventService;
    }

    @EventListener
    public void on(AuditApplicationEvent event) {
        AuditEvent auditEvent = event.getAuditEvent();

        String ipAddress = "Unknown";
        Object details = auditEvent.getData().get("details");
        if (details instanceof WebAuthenticationDetails) {
            ipAddress = ((WebAuthenticationDetails) details).getRemoteAddress();
        }

        AuthEventDto eventDto = new AuthEventDto(ipAddress, OffsetDateTime.ofInstant(auditEvent.getTimestamp(), ZoneId.systemDefault()),
                auditEvent.getPrincipal(), auditEvent.getType());

        LOGGER.debug("Got auth event: [{}]", eventDto);

        authEventService.processEvent(eventDto);
    }

}
