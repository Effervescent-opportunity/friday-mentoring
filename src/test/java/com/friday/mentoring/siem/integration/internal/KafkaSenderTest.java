package com.friday.mentoring.siem.integration.internal;

import com.friday.mentoring.siem.integration.SiemEventType;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaSenderTest {

    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;
    @InjectMocks
    KafkaSender kafkaSender;

    @Test
    public void kafkaDisabledTest() {
        when(kafkaTemplate.send(any(), any())).thenThrow(new RuntimeException());

        assertFalse(kafkaSender.send("ipAddress", OffsetDateTime.now(), "user", SiemEventType.AUTH_FAILURE));
    }

    @Test
    public void kafkaEnabledTest() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(any(), any())).thenReturn(future);

        future.complete(new SendResult<>(new ProducerRecord<>("topic", "success"),
                new RecordMetadata(new TopicPartition("topic", 1), 1, 2, 3, 4, 5)));

        assertTrue(kafkaSender.send("ipAddress", OffsetDateTime.now(), "user", SiemEventType.AUTH_SUCCESS));

        verify(kafkaTemplate).send(any(), any());
    }
}