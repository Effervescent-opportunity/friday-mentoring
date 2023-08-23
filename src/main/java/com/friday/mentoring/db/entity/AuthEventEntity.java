package com.friday.mentoring.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;


@Entity
@Table(name = "auth_event")
public class AuthEventEntity {

    @Id
    private UUID id;


}
