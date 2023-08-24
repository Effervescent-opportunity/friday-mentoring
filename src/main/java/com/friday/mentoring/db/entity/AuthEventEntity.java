package com.friday.mentoring.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * ДТО с событиями аутентификации и авторизации для отправки в Кафку
 *
 * @param ipAddress IPv4 адрес
 * @param time      время события
 * @param userName  логин
 * @param type      вид события
 */
@Entity
@Table(name = "auth_event")
public class AuthEventEntity {
    //String ipAddress, OffsetDateTime time, String userName, String type
    @Id
    private UUID id;


    private String ipAddress;
    private OffsetDateTime eventTime;
    private String userName;
    private String type;

}
