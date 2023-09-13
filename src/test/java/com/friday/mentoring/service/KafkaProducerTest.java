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
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    private final AuthEventDto authEventDto = new AuthEventDto("ipAddress", OffsetDateTime.now(), "user", "type");

    @Test
    public void kafkaDisabled() {//todo check coverage and maybe write tests on InterruptedException and unexpected Exception
        when(adminClient.listTopics(any(ListTopicsOptions.class))).thenThrow(RuntimeException.class); //Checked exception is invalid for this method!

        assertFalse(kafkaProducer.sendAuthEvent(authEventDto));

        verify(kafkaTemplate, never()).send(any(), any());
    }

//    @Test
//    public void kafkaEnabled() {//todo check coverage and maybe write tests on InterruptedException and unexpected Exception
//
//        assertTrue(kafkaProducer.sendAuthEvent(authEventDto));
//
//        verify(kafkaTemplate, never()).send(any(), any());
//    }

}