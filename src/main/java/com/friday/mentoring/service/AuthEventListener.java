package com.friday.mentoring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEventListener.class);

    private final KafkaProducer kafkaProducer;

    public AuthEventListener(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }


    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        LOGGER.info("LALALA success Auth event [{}], [{}]", success.getSource(), success.getAuthentication());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failures) {
        LOGGER.info("LALALA fail Auth event [{}], [{}]", failures.getSource(), failures.getAuthentication());//get source/ get details/get ip
    }

    @EventListener
    public void onFailure(AuthorizationDeniedEvent failure) {
        LOGGER.info("LALALA fail Authorization event [{}], [{}]", failure.getSource(), failure.getAuthentication());
    }
}
