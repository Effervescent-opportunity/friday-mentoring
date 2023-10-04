package com.friday.mentoring.usecase;

import java.time.OffsetDateTime;

public interface SiemSender {

    /**
     * @return true если событие было отправлено, иначе false
     */
    boolean send(String ipAddress, OffsetDateTime time, String userName, SiemEventType eventType);
}
