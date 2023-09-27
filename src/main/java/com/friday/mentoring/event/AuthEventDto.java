package com.friday.mentoring.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AuthEventDto(UUID id, String ipAddress, OffsetDateTime time, String userName, String type) {
}
