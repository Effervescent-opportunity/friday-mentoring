package com.friday.mentoring.usecase;

import com.friday.mentoring.jpa.AuthEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;

public interface EventRepository {
    void save(String ipAddress, OffsetDateTime time, String userName, String type);

    void setSuccessStatus(UUID id);

    Stream<AuthEventEntity> getNotSentEvents();

    Page<AuthEventDto> getFilteredEntities(String userName, String ipAddress, String eventType, OffsetDateTime timeFrom,
                                           OffsetDateTime timeTo, Pageable pageable);

    record AuthEventDto(String ipAddress, OffsetDateTime time, String userName, String eventType) {
    }

}
