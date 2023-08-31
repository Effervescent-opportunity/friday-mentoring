package com.friday.mentoring.db.entity;

import com.friday.mentoring.dto.AuthEventDto;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Таблица в базе для событий, которые нужно отправить в Кафку
 */
@Entity
@Table(name = "outbox")
public class OutboxEntity {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    /**
     * Количество оставшихся попыток переотправки
     */
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 5;
    /**
     * Событие аутентификации или авторизации
     */
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
