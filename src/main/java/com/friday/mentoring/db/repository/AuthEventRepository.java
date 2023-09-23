package com.friday.mentoring.db.repository;

import com.friday.mentoring.db.entity.AuthEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface AuthEventRepository extends JpaRepository<AuthEventEntity, UUID> {

    @Query(value = "SELECT a FROM AuthEvent a WHERE a.wasSent = false")
    Stream<AuthEventEntity> getEventsForSending();//todo how Stream works?

    List<AuthEventEntity> findAllByWasSentFalse();

    @Modifying
    @Query(value = "UPDATE AuthEvent a SET a.wasSent = true WHERE a.id = ?1")
    void setSuccessSentStatus(UUID id);//todo rename method & maybe rename field (send_status?)
}
