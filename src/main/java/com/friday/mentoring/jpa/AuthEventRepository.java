package com.friday.mentoring.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.stream.Stream;

public interface AuthEventRepository extends JpaRepository<AuthEventEntity, UUID>, JpaSpecificationExecutor<AuthEventEntity> {
    Stream<AuthEventEntity> streamByWasSentFalse();

    @Modifying
    @Query(value = "UPDATE AuthEventEntity a SET a.wasSent = true WHERE a.id = :id")
    void setSuccessSentStatus(@Param("id") UUID id);
}
