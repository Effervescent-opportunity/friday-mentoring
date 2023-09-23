package com.friday.mentoring.db.entity;

import com.friday.mentoring.dto.AuthEventDto;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Таблица в базе с событиями аутентификации и авторизации
 */
@Entity
@Table(name = "auth_event")
public class AuthEventEntity {
    @Id
    @GeneratedValue
    private UUID id;
    /**
     * IP адрес
     */
    @Column(name = "ip_address", length = 45, nullable = false)
    private String ipAddress;
    /**
     * Время события
     */
    @Column(name = "event_time", nullable = false)
    private OffsetDateTime eventTime;
    /**
     * Логин
     */
    @Column(name = "user_name", length = 30, nullable = false)
    private String userName;
    /**
     * Вид события
     */
    @Column(name = "event_type", length = 30, nullable = false)
    private String eventType;
    /**
     * Было ли отправлено событие в SIEM
     */
    @Column(name = "was_sent", nullable = false)
    private boolean wasSent;

    public AuthEventEntity() {
    }

    public AuthEventEntity(AuthEventDto eventDto) {
        this.ipAddress = eventDto.ipAddress();
        this.eventTime = eventDto.time();
        this.userName = eventDto.userName();
        this.eventType = eventDto.type();
    }

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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public boolean wasSent() {
        return wasSent;
    }

    public void setWasSent(boolean wasSent) {
        this.wasSent = wasSent;
    }

    @Override
    public String toString() {
        return "AuthEventEntity{" +
                "id=" + id +
                ", ipAddress='" + ipAddress + '\'' +
                ", eventTime=" + eventTime +
                ", userName='" + userName + '\'' +
                ", eventType='" + eventType + '\'' +
                ", wasSent=" + wasSent +
                '}';
    }
}
