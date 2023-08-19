package com.friday.mentoring.dto;

import java.time.OffsetDateTime;

/**
 * ДТО с событиями аутентификации и авторизации для отправки в Кафку
 *
 * @param ipAddress IPv4 адрес
 * @param time      время события
 * @param userName  логин
 * @param type      вид события
 */
public record AuthEventDto(String ipAddress, OffsetDateTime time, String userName, String type) {
}
