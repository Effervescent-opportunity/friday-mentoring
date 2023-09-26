package com.friday.mentoring.siem.integration;

import java.time.OffsetDateTime;

public interface SiemSender {//todo rename if I have a good idea
    //this is public api and realization must be package private

    /**
     * @return true если событие было отправлено, иначе false
     */
    boolean send(String ipAddress, OffsetDateTime time, String userName, SiemEventType eventType);

    enum SiemEventType {//sorry I love all 3 of them
        //todo ask, can I leave all 3 (task4)
        // > С помощью Spring Kafka нужно реализовать отправку событий:        //об успешном входе;        //о неуспешном входе.
        AUTHENTICATION_SUCCESS,
        AUTHENTICATION_FAILURE,
        AUTHORIZATION_FAILURE,
    }

}
