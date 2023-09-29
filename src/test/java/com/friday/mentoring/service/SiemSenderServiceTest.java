package com.friday.mentoring.service;

import com.friday.mentoring.event.AuthEventType;
import com.friday.mentoring.event.repository.AuthEventReader;
import com.friday.mentoring.event.repository.AuthEventSaver;
import com.friday.mentoring.event.repository.internal.AuthEventEntity;
import com.friday.mentoring.siem.integration.SiemEventType;
import com.friday.mentoring.siem.integration.SiemSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SiemSenderServiceTest {

    @Mock
    private SiemSender siemSender;
    @Mock
    private AuthEventSaver authEventSaver;
    @Mock
    private AuthEventReader authEventReader;
    @InjectMocks
    private SiemSenderService siemSenderService;

    @AfterEach
    void tearDown() {
        Mockito.reset(siemSender, authEventSaver, authEventReader);
    }

    @Test
    public void noEntitiesTest() {
        when(authEventReader.getNotSentEvents()).thenReturn(Stream.empty());

        siemSenderService.sendToSiem();

        verify(siemSender, never()).send(any(), any(), any(), any());
        verify(authEventSaver, never()).setSuccessStatus(any());
    }

    @Test
    public void exceptionTest() {
        AuthEventEntity authEvent = new AuthEventEntity("127.0.0.1", OffsetDateTime.now(), "root", AuthEventType.AUTHORIZATION_FAILURE);

        when(authEventReader.getNotSentEvents()).thenReturn(Stream.of(authEvent));
        when(siemSender.send(any(), any(), any(), any())).thenThrow(RuntimeException.class);

        siemSenderService.sendToSiem();

        verify(siemSender, times(1)).send(authEvent.getIpAddress(), authEvent.getEventTime(), authEvent.getUserName(), SiemEventType.AUTH_FAILURE);
        verify(authEventSaver, never()).setSuccessStatus(any());
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
        AuthEventEntity authEvent = new AuthEventEntity("127.0.0.1", OffsetDateTime.now(), "root", AuthEventType.AUTHENTICATION_FAILURE);

        when(authEventReader.getNotSentEvents()).thenReturn(Stream.of(authEvent));
        when(siemSender.send(any(), any(), any(), any())).thenReturn(wasSent);

        siemSenderService.sendToSiem();

        verify(siemSender, times(1)).send(authEvent.getIpAddress(), authEvent.getEventTime(), authEvent.getUserName(), SiemEventType.AUTH_FAILURE);
        verify(authEventSaver, times(wasSent ? 1 : 0)).setSuccessStatus(any());
    }

}