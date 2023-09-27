package com.friday.mentoring.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Бины для настройки бд и ее сущностей
 */
@Configuration
public class CustomDbConfig {//todo make config package private and move to event.repository.internal
    @Bean
    HibernatePropertiesCustomizer jsonFormatMapperCustomizer(ObjectMapper objectMapper) {
        return (properties) -> properties.put(AvailableSettings.JSON_FORMAT_MAPPER,
                new JacksonJsonFormatMapper(objectMapper));
    }
    /*
     Какие у меня юз кейсы (сценарии использования)?
     1. получить данные часов
     2. вход с логином\паролем
     3. отправка данных входа в сием

     база внутренняя типа для 2 и 3

     то есть чтобы было видно из пакетов, надо сделать 4 основных пакета минимум
     1. clock
     2. authorized access ??
     3. siem integration (or just sender)
     4. events repository (input and output)

     (и проверить, что оно работает, и написать тесты, эй. сделай пока базу и отправку пакетами и чтобы работало and tests)
     */
}
