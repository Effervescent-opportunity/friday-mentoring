package com.friday.mentoring.service;

import com.friday.mentoring.db.entity.AuthEventEntity;
import com.friday.mentoring.db.entity.OutboxEntity;
import com.friday.mentoring.db.repository.AuthEventRepository;
import com.friday.mentoring.db.repository.OutboxRepository;
import com.friday.mentoring.dto.AuthEventDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthEventServiceTest {

    private static final String IP_ADDRESS = "127.0.0.1";
    private static final String USERNAME = "root";
    private static final String EVENT_TYPE = "AUTHENTICATION_SUCCESS";
    private static final OffsetDateTime EVENT_TIME = OffsetDateTime.now(ZoneId.systemDefault());

    @Spy
    AuthEventRepository authEventRepository;
    @Spy
    OutboxRepository outboxRepository;
    @Spy
    TransactionTemplate transactionTemplate;
    @Mock
    KafkaProducer kafkaProducer;

    @InjectMocks
    AuthEventService authEventService;

    @Test
    public void eventWasSentTest() {
        AuthEventDto eventDto = createAuthEventDto();

        doCallRealMethod().when(transactionTemplate).executeWithoutResult(any());

        doAnswer(invocation -> {
            if (invocation.getArgument(0) instanceof TransactionCallback transactionCallback) {
                transactionCallback.doInTransaction(null);
            } else {
                fail("TransactionalCallback expected");
            }
            return null;
        }).when(transactionTemplate).execute(any());

        doAnswer(invocation -> {
            if (invocation.getArgument(0) instanceof AuthEventEntity authEventEntity) {
                assertEquals(IP_ADDRESS, authEventEntity.getIpAddress());
                assertEquals(USERNAME, authEventEntity.getUserName());
                assertEquals(EVENT_TYPE, authEventEntity.getEventType());
                assertEquals(EVENT_TIME, authEventEntity.getEventTime());
            } else {
                fail("AuthEventEntity expected");
            }
            return null;
        }).when(authEventRepository).save(any());

        doAnswer(invocation -> {
            if (invocation.getArgument(0) instanceof OutboxEntity outboxEntity) {
                assertEquals(5, outboxEntity.getRetryCount());
                assertEquals(eventDto, outboxEntity.getEvent());
                assertTrue(EVENT_TIME.isBefore(outboxEntity.getCreatedAt()));
            } else {
                fail("OutboxEntity expected");
            }
            return null;
        }).when(outboxRepository).save(any());

        when(kafkaProducer.sendAuthEvent(any(AuthEventDto.class))).thenReturn(true);

        authEventService.processEvent(eventDto);

        verify(authEventRepository).save(any(AuthEventEntity.class));
        verify(outboxRepository).save(any(OutboxEntity.class));
        verify(outboxRepository).deleteById(isNull());
        verify(kafkaProducer).sendAuthEvent(eventDto);
    }

    @Test
    public void eventWasNotSentTest() {
        AuthEventDto eventDto = createAuthEventDto();
        AtomicInteger outboxSaveCount = new AtomicInteger();

        doCallRealMethod().when(transactionTemplate).executeWithoutResult(any());

        doAnswer(invocation -> {
            if (invocation.getArgument(0) instanceof TransactionCallback transactionCallback) {
                transactionCallback.doInTransaction(null);
            } else {
                fail("TransactionalCallback expected");
            }
            return null;
        }).when(transactionTemplate).execute(any());

        doAnswer(invocation -> {
            if (invocation.getArgument(0) instanceof AuthEventEntity authEventEntity) {
                assertEquals(IP_ADDRESS, authEventEntity.getIpAddress());
                assertEquals(USERNAME, authEventEntity.getUserName());
                assertEquals(EVENT_TYPE, authEventEntity.getEventType());
                assertEquals(EVENT_TIME, authEventEntity.getEventTime());
            } else {
                fail("AuthEventEntity expected");
            }
            return null;
        }).when(authEventRepository).save(any());

        doAnswer(invocation -> {
            if (invocation.getArgument(0) instanceof OutboxEntity outboxEntity) {
                if (outboxSaveCount.get() == 0) {
                    assertEquals(5, outboxEntity.getRetryCount());
                    outboxSaveCount.getAndIncrement();
                } else {
                    assertEquals(4, outboxEntity.getRetryCount());
                }
                assertEquals(eventDto, outboxEntity.getEvent());
                assertTrue(EVENT_TIME.isBefore(outboxEntity.getCreatedAt()));
            } else {
                fail("OutboxEntity expected");
            }
            return null;
        }).when(outboxRepository).save(any());

        when(kafkaProducer.sendAuthEvent(any(AuthEventDto.class))).thenReturn(false);

        authEventService.processEvent(eventDto);

        verify(authEventRepository).save(any(AuthEventEntity.class));
        verify(outboxRepository, times(2)).save(any(OutboxEntity.class));
        verify(outboxRepository, never()).deleteById(any(UUID.class));
        verify(kafkaProducer).sendAuthEvent(eventDto);
    }

    @Test()
    public void transactionErrorTest() {
        AuthEventDto eventDto = createAuthEventDto();

        doCallRealMethod().when(transactionTemplate).executeWithoutResult(any());

        doAnswer(invocation -> {
            if (invocation.getArgument(0) instanceof TransactionCallback) {
                throw new UnexpectedRollbackException("message");
            } else {
                fail("TransactionalCallback expected");
            }
            return null;
        }).when(transactionTemplate).execute(any());

        UnexpectedRollbackException thrown = assertThrows(UnexpectedRollbackException.class, () -> authEventService.processEvent(eventDto));

        assertEquals("message", thrown.getMessage());

        verify(authEventRepository, never()).save(any(AuthEventEntity.class));
        verify(outboxRepository, never()).save(any(OutboxEntity.class));
        verify(outboxRepository, never()).deleteById(any(UUID.class));
        verify(kafkaProducer, never()).sendAuthEvent(eventDto);
    }

    private AuthEventDto createAuthEventDto() {
        return new AuthEventDto(IP_ADDRESS, EVENT_TIME, USERNAME, EVENT_TYPE);
    }

}