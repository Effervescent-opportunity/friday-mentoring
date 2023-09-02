package com.friday.mentoring.service;

import com.friday.mentoring.db.entity.OutboxEntity;
import com.friday.mentoring.db.repository.OutboxRepository;
import com.friday.mentoring.dto.AuthEventDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

import static com.friday.mentoring.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OutboxRetryServiceTest {

    @Mock
    private KafkaProducer kafkaProducer;
    @Mock
    private OutboxRepository outboxRepository;
    @InjectMocks
    private OutboxRetryService outboxRetryService;

    @AfterEach
    void tearDown() {
        Mockito.reset(kafkaProducer, outboxRepository);
    }

    @Test
    public void noEntitiesTest() {
        when(outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(anyInt(), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(Collections.emptyList());

        outboxRetryService.retrySendingToKafka();

        verify(kafkaProducer, never()).sendAuthEvent(any(AuthEventDto.class));
        verify(outboxRepository, never()).deleteById(any(UUID.class));
        verify(outboxRepository, never()).save(any());
    }

    @Test
    public void wasSentTest() {
        sendToKafka(true);
    }

    @Test
    public void wasNotSentTest() {
        sendToKafka(false);
    }

    private void sendToKafka(boolean wasSent) {
        AuthEventDto authEvent = new AuthEventDto(LOCAL_IP_ADDRESS, OffsetDateTime.now(), ROOT_USERNAME, AUTHENTICATION_FAILURE_TYPE);

        when(outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(anyInt(), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(Collections.singletonList(new OutboxEntity(authEvent)));
        when(kafkaProducer.sendAuthEvent(any(AuthEventDto.class))).thenReturn(wasSent);

        Mockito.doAnswer((invocationOnMock) -> {
            OutboxEntity outboxEntity = invocationOnMock.getArgument(0, OutboxEntity.class);
            assertEquals(4, outboxEntity.getRetryCount());
            assertEquals(authEvent, outboxEntity.getEvent());
            return null;
        }).when(outboxRepository).save(any(OutboxEntity.class));

        outboxRetryService.retrySendingToKafka();

        verify(kafkaProducer, times(1)).sendAuthEvent(authEvent);
        verify(outboxRepository, times(wasSent ? 1 : 0)).deleteById(any());
        verify(outboxRepository, times(wasSent ? 0 : 1)).save(any());
    }

}