package com.friday.mentoring.db.entity;

import jakarta.persistence.*;

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
//    @GeneratedValue(strategy = GenerationType.UUID) todo test this and below
    @GeneratedValue
    private UUID id;
    //todo test without columns without initdb script & with columns and db script & ... - just clean docker volumes & other for postgres
    @Column(name = "ip_address", length = 45, nullable = false)
    private String ipAddress;
    @Column(name = "event_time", nullable = false)
    private OffsetDateTime eventTime;
    @Column(name = "user_name", length = 20, nullable = false)
    private String userName;
    @Column(name = "type", length = 20, nullable = false)
    private String type;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public OffsetDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(OffsetDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
