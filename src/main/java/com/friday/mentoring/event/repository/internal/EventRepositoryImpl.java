package com.friday.mentoring.event.repository.internal;

import com.friday.mentoring.event.AuthEventType;
import com.friday.mentoring.event.repository.AuthEventReader;
import com.friday.mentoring.event.repository.AuthEventSaver;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;

@Component
class EventRepositoryImpl implements AuthEventSaver, AuthEventReader {

    private final AuthEventRepository authEventRepository;

    public EventRepositoryImpl(AuthEventRepository authEventRepository) {
        this.authEventRepository = authEventRepository;
    }

    @Override
    public void save(String ipAddress, OffsetDateTime time, String userName, AuthEventType type) {
        authEventRepository.save(new AuthEventEntity(ipAddress, time, userName, type));
    }

    @Override
    public void setSuccessStatus(UUID id) {
        authEventRepository.setSuccessSentStatus(id);
    }

    @Override
    public Stream<AuthEventEntity> getNotSentEvents() {
        return authEventRepository.streamByWasSentFalse();
    }
}
