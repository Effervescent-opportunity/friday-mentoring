package com.friday.mentoring.service;

import com.friday.mentoring.dto.AuthEventDto;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {

    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;
    @InjectMocks
    KafkaProducer kafkaProducer;

    private final AuthEventDto eventDto = new AuthEventDto("ipAddress", OffsetDateTime.now(), "user", "type");

    @Test
    public void kafkaDisabledTest() {
        when(kafkaTemplate.send(anyString(), any(AuthEventDto.class))).thenThrow(new RuntimeException());

        assertFalse(kafkaProducer.sendAuthEvent(eventDto));

        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    public void kafkaEnabledTest() {
//todo this https://stackoverflow.com/questions/57475464/how-to-mock-result-from-kafkatemplate
    }
}