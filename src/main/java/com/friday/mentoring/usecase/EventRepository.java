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

    Page<AuthEventEntity> getFilteredEntities(String user, String ip, String type, OffsetDateTime dateFrom,
                                              OffsetDateTime dateTo, int page, int size, String[] sort);

    Page<AuthEventEntity> getFilteredEntities1(String user, String ip, String type, OffsetDateTime dateFrom,
                                               OffsetDateTime dateTo, Pageable pageable);//todo delete pageable, make entity -> dto mapping, написать тесты!
}
