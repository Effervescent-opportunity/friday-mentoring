package com.friday.mentoring.db.entity;

import com.friday.mentoring.dto.AuthEventDto;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * outbox table for auth events - they will be deleted after sending
 */
@Entity
@Table(name = "outbox")
public class OutboxEntity {
//without initdb: alter table if exists auth_event alter column event_time set data type timestamp(6) with time zone
//without initdb: create table outbox (id uuid not null, created_at timestamp(6) with time zone not null, event json not null, retry_count integer not null, primary key (id))
    @Id
//    @GeneratedValue(strategy = GenerationType.UUID) todo test this and below
    @GeneratedValue
    private UUID id;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 5;

    //    private UUID eventId;
    @Type(JsonType.class)
    @Column(name = "event", nullable = false, columnDefinition = "json")
    private AuthEventDto event;

    public OutboxEntity() {
    }

    public OutboxEntity(AuthEventDto eventDto) {
        this.createdAt = OffsetDateTime.now(ZoneId.systemDefault());
        this.event = eventDto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public AuthEventDto getEvent() {
        return event;
    }

    public void setEvent(AuthEventDto event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "OutboxEntity{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", retryCount=" + retryCount +
                ", event=" + event +
                '}';
    }
}
