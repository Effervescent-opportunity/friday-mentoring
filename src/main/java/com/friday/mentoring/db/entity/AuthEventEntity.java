package com.friday.mentoring.db.entity;

import com.friday.mentoring.dto.AuthEventDto;
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
    //with columns & without script - works
    //with columns & with script - works and recreates columns and ruins its types
    //todo run with columns & with script as is now
    //todo run with columns & with script & with ddl-auto not update
    //2023-08-28T09:17:47.531+03:00 DEBUG 9830 --- [           main] org.hibernate.SQL                        : alter table if exists auth_event alter column event_time set data type timestamp(6) with time zone
    //Hibernate: alter table if exists auth_event alter column event_time set data type timestamp(6) with time zone
    //2023-08-28T09:17:47.534+03:00 DEBUG 9830 --- [           main] org.hibernate.SQL                        : alter table if exists auth_event alter column user_name set data type varchar(20)
    //Hibernate: alter table if exists auth_event alter column user_name set data type varchar(20)
    //2023-08-28T09:17:47.549+03:00 DEBUG 9830 --- [           main] org.hibernate.SQL                        : alter table if exists outbox alter column created_at set data type timestamp(6) with time zone
    //Hibernate: alter table if exists outbox alter column created_at set data type timestamp(6) with time zone
    //2023-08-28T09:17:47.552+03:00 DEBUG 9830 --- [           main] org.hibernate.SQL                        : alter table if exists outbox add column event json not null
    //Hibernate: alter table if exists outbox add column event json not null
    //2023-08-28T09:17:47.554+03:00 DEBUG 9830 --- [           main] org.hibernate.SQL                        : alter table if exists outbox alter column retry_count set data type integer
    @Column(name = "ip_address", length = 45, nullable = false)
    private String ipAddress;
    @Column(name = "event_time", nullable = false)
    private OffsetDateTime eventTime;
    @Column(name = "user_name", length = 30, nullable = false)
    private String userName;
    @Column(name = "type", length = 30, nullable = false)
    private String type;

    public AuthEventEntity() {
    }

    public AuthEventEntity(AuthEventDto eventDto) {
        this.ipAddress = eventDto.ipAddress();
        this.eventTime = eventDto.time();
        this.userName = eventDto.userName();
        this.type = eventDto.type();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AuthEventEntity{" +
                "id=" + id +
                ", ipAddress='" + ipAddress + '\'' +
                ", eventTime=" + eventTime +
                ", userName='" + userName + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
