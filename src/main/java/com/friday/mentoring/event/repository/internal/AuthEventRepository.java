package com.friday.mentoring.event.repository.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;
import java.util.stream.Stream;

public interface AuthEventRepository extends JpaRepository<AuthEventEntity, UUID> {
    Stream<AuthEventEntity> streamByWasSentFalse();

    @Modifying
    @Query(value = "UPDATE AuthEventEntity a SET a.wasSent = true WHERE a.id = ?1")
    void setSuccessSentStatus(UUID id);
}
