package com.friday.mentoring.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.Specification;

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

    public AuthEventEntity(String ipAddress, OffsetDateTime eventTime, String userName, String eventType) {
        this.ipAddress = ipAddress;
        this.eventTime = eventTime;
        this.userName = userName;
        this.eventType = eventType;
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

    public static Specification<AuthEventEntity> userNameEquals(String userName) {
        return (root, query, criteriaBuilder) -> {
            if (userName == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("userName"), userName);
        };
    }

    public static Specification<AuthEventEntity> ipAddressEquals(String ipAddress) {
        return (root, query, criteriaBuilder) -> {
            if (ipAddress == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("ipAddress"), ipAddress);
        };
    }

    public static Specification<AuthEventEntity> eventTypeEquals(String eventType) {
        return (root, query, criteriaBuilder) -> {
            if (eventType == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("eventType"), eventType);
        };
    }

    public static Specification<AuthEventEntity> eventTimeGreaterThanOrEquals(OffsetDateTime eventTimeFrom) {
        return (root, query, criteriaBuilder) -> {
            if (eventTimeFrom == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("eventTime"), eventTimeFrom);
        };
    }

    public static Specification<AuthEventEntity> eventTimeLessThanOrEquals(OffsetDateTime eventTimeTo) {
        return (root, query, criteriaBuilder) -> {
            if (eventTimeTo == null) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("eventTime"), eventTimeTo);
        };
    }
}
