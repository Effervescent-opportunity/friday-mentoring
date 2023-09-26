package com.friday.mentoring.event.repository;

import com.friday.mentoring.db.entity.AuthEventEntity;
import com.friday.mentoring.db.repository.AuthEventRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
class AuthEventSaverImpl implements AuthEventSaver {

    private final AuthEventRepository authEventRepository;

    public AuthEventSaverImpl(AuthEventRepository authEventRepository) {
        this.authEventRepository = authEventRepository;
    }

    @Override
    @Transactional
    public void save(String ipAddress, OffsetDateTime time, String userName, AuthEventType type) {
        authEventRepository.save(new AuthEventEntity(ipAddress, time, userName, type.name()));
    }

    @Override
    public void setSuccessStatus(UUID id) {
        authEventRepository.setSuccessSentStatus(id);
    }
}
