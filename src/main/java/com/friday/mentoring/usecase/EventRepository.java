package com.friday.mentoring.usecase;

import com.friday.mentoring.todo.AuthEventType;
import com.friday.mentoring.jpa.AuthEventEntity;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;

public interface EventRepository {
    void save(String ipAddress, OffsetDateTime time, String userName, AuthEventType type);

    void setSuccessStatus(UUID id);

    Stream<AuthEventEntity> getNotSentEvents();
}
