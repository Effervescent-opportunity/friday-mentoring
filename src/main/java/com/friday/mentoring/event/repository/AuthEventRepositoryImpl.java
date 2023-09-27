package com.friday.mentoring.event.repository;

import com.friday.mentoring.db.entity.AuthEventEntity;
import com.friday.mentoring.db.repository.AuthEventRepository;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;

@Component
class AuthEventRepositoryImpl implements AuthEventSaver, AuthEventReader {//todo rename - this is very awful

    private final AuthEventRepository authEventRepository;

    public AuthEventRepositoryImpl(AuthEventRepository authEventRepository) {
        this.authEventRepository = authEventRepository;
    }

    @Override
    public void save(String ipAddress, OffsetDateTime time, String userName, AuthEventType type) {
        authEventRepository.save(new AuthEventEntity(ipAddress, time, userName, type.name()));
    }

    @Override
    public void setSuccessStatus(UUID id) {
        authEventRepository.setSuccessSentStatus(id);
    }

    @Override
    public Stream<AuthEventEntity> getNotSentEvents() {
        return authEventRepository.getEventsForSending();
    }
}
