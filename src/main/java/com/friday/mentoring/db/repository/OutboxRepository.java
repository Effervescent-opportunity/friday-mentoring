package com.friday.mentoring.db.repository;

import com.friday.mentoring.db.entity.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {
    List<OutboxEntity> findTop10ByRetryCountGreaterThanAndCreatedAtBetween(Integer minRetryCount, OffsetDateTime from, OffsetDateTime to);
}
