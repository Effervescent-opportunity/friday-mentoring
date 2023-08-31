package com.friday.mentoring.db.repository;

import com.friday.mentoring.db.entity.AuthEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthEventRepository extends JpaRepository<AuthEventEntity, UUID> {

}
