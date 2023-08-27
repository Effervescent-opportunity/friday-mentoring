package com.friday.mentoring.db.repository;

import com.friday.mentoring.db.entity.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {

    List<OutboxEntity> findTop10ByCreatedAtBetween(OffsetDateTime from, OffsetDateTime to);
}
