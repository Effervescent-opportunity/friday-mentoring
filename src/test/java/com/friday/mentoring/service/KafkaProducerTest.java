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
    @Mock
    AdminClient adminClient;
    @InjectMocks
    KafkaProducer kafkaProducer;

    private final AuthEventDto eventDto = new AuthEventDto("ipAddress", OffsetDateTime.now(), "user", "type");

    @Test
    public void kafkaDisabledTest() {
        when(adminClient.listTopics(any(ListTopicsOptions.class))).thenThrow(RuntimeException.class);

        assertFalse(kafkaProducer.sendAuthEvent(eventDto));

        verify(kafkaTemplate, never()).send(any(), any());
    }
}