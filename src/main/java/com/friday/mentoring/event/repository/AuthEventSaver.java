package com.friday.mentoring.event.repository;

import com.friday.mentoring.event.AuthEventType;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface AuthEventSaver {//todo rename - this is awful
    void save(String ipAddress, OffsetDateTime time, String userName, AuthEventType type);

    void setSuccessStatus(UUID id);

}
