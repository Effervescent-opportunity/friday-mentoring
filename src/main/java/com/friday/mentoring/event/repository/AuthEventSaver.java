package com.friday.mentoring.event.repository;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface AuthEventSaver {//todo rename - this is awful
    //but I have to save and to get. Can I do only save? Try now and fix later maybe
    void save(String ipAddress, OffsetDateTime time, String userName, AuthEventType type);

    void setSuccessStatus(UUID id);//todo isn't it too much - id is inner thing

    enum AuthEventType {
        AUTHENTICATION_SUCCESS,
        AUTHENTICATION_FAILURE,
        AUTHORIZATION_FAILURE,
    }

}
