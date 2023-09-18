package com.friday.mentoring.service;

import com.friday.mentoring.dto.AuthEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    @Mock KafkaTemplate<String, Object> kafkaTemplate;
    @InjectMocks KafkaProducer kafkaProducer;

    final AuthEventDto eventDto = new AuthEventDto("ipAddress", OffsetDateTime.now(), "user", "type");

    @BeforeEach
    void setUp() {
        Mockito.reset(kafkaTemplate);
    }

    @Test
    public void kafkaDisabledTest() {
        when(kafkaTemplate.send(any(), any())).thenThrow(RuntimeException.class);
        assertFalse(kafkaProducer.sendAuthEvent(eventDto));
    }

    @Test
    @Disabled("todo: см. https://stackoverflow.com/questions/57475464")
    public void kafkaEnabledTest() {
        assertTrue(kafkaProducer.sendAuthEvent(eventDto));
    }
}