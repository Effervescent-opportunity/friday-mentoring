package com.friday.mentoring.event.repository.internal;

import com.friday.mentoring.event.repository.internal.AuthEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface AuthEventRepository extends JpaRepository<AuthEventEntity, UUID> {
//todo rename method
    @Query(value = "SELECT a FROM AuthEventEntity a WHERE a.wasSent = false")
    Stream<AuthEventEntity> getEventsForSending();

    List<AuthEventEntity> findAllByWasSentFalse();

    @Modifying
    @Query(value = "UPDATE AuthEventEntity a SET a.wasSent = true WHERE a.id = ?1")
    void setSuccessSentStatus(UUID id);//todo rename method & maybe rename field (send_status?)
}
