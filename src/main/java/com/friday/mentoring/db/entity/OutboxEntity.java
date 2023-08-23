package com.friday.mentoring.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * outbox table for auth events - they will be deleted after sending
 */
@Entity
@Table(name = "outbox")
public class OutboxEntity {


}
