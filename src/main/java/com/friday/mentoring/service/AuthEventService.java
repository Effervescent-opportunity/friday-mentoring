package com.friday.mentoring.service;

import com.friday.mentoring.db.entity.AuthEventEntity;
import com.friday.mentoring.db.repository.AuthEventRepository;
import com.friday.mentoring.dto.AuthEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Обрабатывает события аудита (сохраняет в базу, отправляет в Кафку)
 */
@Component
public class AuthEventService {//todo del - it's unneeded

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthEventService.class);

    private final AuthEventRepository authEventRepository;

    public AuthEventService(AuthEventRepository authEventRepository) {
        this.authEventRepository = authEventRepository;
    }

    @Transactional
    public void processEvent(AuthEventDto authEventDto) {
        AuthEventEntity authEventEntity = new AuthEventEntity(authEventDto);
        authEventRepository.save(authEventEntity);

        LOGGER.debug("AuthEventEntity [{}] was saved", authEventEntity);
    }

}
