package com.friday.mentoring.service;

import com.friday.mentoring.dto.AuthEventDto;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        when(kafkaTemplate.send(any(), any(AuthEventDto.class))).thenThrow(new RuntimeException());

        assertFalse(kafkaProducer.sendAuthEvent(eventDto));
    }

    @Test
    public void kafkaEnabledTest() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(any(), any(AuthEventDto.class))).thenReturn(future);
        future.complete(new SendResult<>(new ProducerRecord<>("topic", "success"),
                new RecordMetadata(new TopicPartition("topic", 1), 1, 2, 3, 4, 5)));

        assertTrue(kafkaProducer.sendAuthEvent(eventDto));

        verify(kafkaTemplate).send(any(), eq(eventDto));
//todo this https://stackoverflow.com/questions/57475464/how-to-mock-result-from-kafkatemplate
    }
}