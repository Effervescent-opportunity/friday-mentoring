package com.friday.mentoring.event.repository;

import com.friday.mentoring.event.repository.internal.AuthEventEntity;

import java.util.stream.Stream;

public interface AuthEventReader {

    Stream<AuthEventEntity> getNotSentEvents();
}
