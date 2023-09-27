package com.friday.mentoring.siem.integration;

import java.time.OffsetDateTime;

public interface SiemSender {//todo rename if I have a good idea

    /**
     * @return true если событие было отправлено, иначе false
     */
    boolean send(String ipAddress, OffsetDateTime time, String userName, SiemEventType eventType);

    enum SiemEventType {
        AUTH_SUCCESS,
        AUTH_FAILURE,
    }

    //it works but looks worse :(
}
