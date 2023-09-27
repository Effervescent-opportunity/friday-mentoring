package com.friday.mentoring.siem.integration.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * Сериализатор для сообщений, отправляющихся в Кафку - сериализует даты в строку, а не в массив, как стандартный Кафковский
 */
public class CustomJsonSerializer extends JsonSerializer<Object> {//it has to be public

    public CustomJsonSerializer() {
        super(customizedObjectMapper());
    }

    private static ObjectMapper customizedObjectMapper() {
        ObjectMapper mapper = JacksonUtils.enhancedObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

}
