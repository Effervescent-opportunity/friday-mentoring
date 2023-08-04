package com.friday.mentoring.util;

import com.friday.mentoring.dto.AuthEventDto;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public class AuthEventDtoDeserializer extends JsonDeserializer<AuthEventDto> {
    public AuthEventDtoDeserializer() {
        super(AuthEventDto.class);
    }
}
