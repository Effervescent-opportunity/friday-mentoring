package com.friday.mentoring.siem.scheduled;

import com.friday.mentoring.jpa.AuthEventEntity;
import com.friday.mentoring.usecase.AuthEventType;
import com.friday.mentoring.usecase.SiemEventType;
import com.friday.mentoring.usecase.EventRepository;
import com.friday.mentoring.usecase.SiemSender;
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
public class ScheduledSiemSenderTest {

    @Mock
    private SiemSender siemSender;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private ScheduledSiemSender scheduledSiemSender;

    @AfterEach
    void tearDown() {
        Mockito.reset(siemSender, eventRepository);
    }

    @Test
    public void noEntitiesTest() {
        when(eventRepository.getNotSentEvents()).thenReturn(Stream.empty());

        scheduledSiemSender.sendToSiem();

        verify(siemSender, never()).send(any(), any(), any(), any());
        verify(eventRepository, never()).setSuccessStatus(any());
    }

    @Test
    public void exceptionTest() {
        AuthEventEntity authEvent = new AuthEventEntity("127.0.0.1", OffsetDateTime.now(), "root", AuthEventType.AUTHORIZATION_FAILURE.getSpringName());

        when(eventRepository.getNotSentEvents()).thenReturn(Stream.of(authEvent));
        when(siemSender.send(any(), any(), any(), any())).thenThrow(RuntimeException.class);

        scheduledSiemSender.sendToSiem();

        verify(siemSender, times(1)).send(authEvent.getIpAddress(), authEvent.getEventTime(), authEvent.getUserName(), SiemEventType.AUTH_FAILURE);
        verify(eventRepository, never()).setSuccessStatus(any());
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
        AuthEventEntity authEvent = new AuthEventEntity("127.0.0.1", OffsetDateTime.now(), "root", AuthEventType.AUTHENTICATION_FAILURE.getSpringName());

        when(eventRepository.getNotSentEvents()).thenReturn(Stream.of(authEvent));
        when(siemSender.send(any(), any(), any(), any())).thenReturn(wasSent);

        scheduledSiemSender.sendToSiem();

        verify(siemSender, times(1)).send(authEvent.getIpAddress(), authEvent.getEventTime(), authEvent.getUserName(), SiemEventType.AUTH_FAILURE);
        verify(eventRepository, times(wasSent ? 1 : 0)).setSuccessStatus(any());
    }

}