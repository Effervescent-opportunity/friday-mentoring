package com.friday.mentoring.service;

import com.friday.mentoring.dto.AuthEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class AuthEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEventListener.class);

    private final KafkaProducer kafkaProducer;

    public AuthEventListener(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @EventListener
    public void on(AuditApplicationEvent event) {
        AuditEvent auditEvent = event.getAuditEvent();

        String ipAddress = "Unknown";
        Object details = auditEvent.getData().get("details");
        if (details instanceof WebAuthenticationDetails) {
            ipAddress = ((WebAuthenticationDetails) details).getRemoteAddress();
        }

        //((WebAuthenticationDetails) auditEvent.getData().get("details")).getRemoteAddress()
        AuthEventDto eventDto = new AuthEventDto(ipAddress, LocalDateTime.ofInstant(auditEvent.getTimestamp(), ZoneId.systemDefault()),
                auditEvent.getPrincipal(), auditEvent.getType());

        LOGGER.info("LALALA1 auditEvent [{}]", eventDto);

        kafkaProducer.sendAuthEvent(eventDto);
    }
//2023-08-03T08:51:49.230+03:00  INFO 10703 --- [0.1-8443-exec-6] c.f.mentoring.service.AuthEventListener  : LALALA1 auditEvent [org.springframework.boot.actuate.audit.listener.AuditApplicationEvent[source=AuditEvent [timestamp=2023-08-03T05:51:49.229693038Z, principal=other, type=AUTHENTICATION_SUCCESS, data={details=WebAuthenticationDetails [RemoteIpAddress=127.0.0.1, SessionId=null]}]]]
    //2023-08-03T08:51:55.359+03:00  INFO 10703 --- [0.1-8443-exec-1] c.f.mentoring.service.AuthEventListener  : LALALA1 auditEvent [org.springframework.boot.actuate.audit.listener.AuditApplicationEvent[source=AuditEvent [timestamp=2023-08-03T05:51:55.359251666Z, principal=root, type=AUTHENTICATION_SUCCESS, data={details=WebAuthenticationDetails [RemoteIpAddress=127.0.0.1, SessionId=null]}]]]
    //2023-08-03T08:53:27.770+03:00  INFO 10703 --- [0.1-8443-exec-3] c.f.mentoring.service.AuthEventListener  : LALALA1 auditEvent [org.springframework.boot.actuate.audit.listener.AuditApplicationEvent[source=AuditEvent [timestamp=2023-08-03T05:53:27.770788186Z, principal=other, type=AUTHORIZATION_FAILURE, data={details=WebAuthenticationDetails [RemoteIpAddress=127.0.0.1, SessionId=null]}]]]
    //2023-08-03T08:55:34.763+03:00  INFO 10703 --- [.1-8443-exec-10] c.f.mentoring.service.AuthEventListener  : LALALA1 auditEvent [org.springframework.boot.actuate.audit.listener.AuditApplicationEvent[source=AuditEvent [timestamp=2023-08-03T05:55:34.763530629Z, principal=root, type=AUTHENTICATION_FAILURE, data={type=org.springframework.security.authentication.BadCredentialsException, message=Bad credentials, details=WebAuthenticationDetails [RemoteIpAddress=127.0.0.1, SessionId=null]}]]]

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        LOGGER.info("LALALA success Auth event [{}], [{}]", success.getSource(), success.getAuthentication());
    }//WebAuthenticationDetails [RemoteIpAddress=127.0.0.1, SessionId=null]

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failures) {
        LOGGER.info("LALALA fail Auth event [{}], [{}]", failures.getSource(), failures.getAuthentication());//get source/ get details/get ip
    }

    @EventListener
    public void onFailure(AuthorizationDeniedEvent failure) {
        LOGGER.info("LALALA fail Authorization event [{}], [{}]", failure.getSource(), failure.getAuthentication());
    }
}
