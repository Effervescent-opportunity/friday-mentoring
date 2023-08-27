package com.friday.mentoring.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * outbox table for auth events - they will be deleted after sending
 */
@Entity
@Table(name = "outbox")
public class OutboxEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.UUID) todo test this and below
    @GeneratedValue
    private UUID id;

    private OffsetDateTime createdAt;

    private Integer retryCount;

    private


}
