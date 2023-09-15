package com.friday.mentoring.db.repository;

import com.friday.mentoring.BaseDatabaseIntegrationTest;
import com.friday.mentoring.db.entity.AuthEventEntity;
import com.friday.mentoring.dto.AuthEventDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuthEventRepositoryTest extends BaseDatabaseIntegrationTest {

    @Autowired
    AuthEventRepository authEventRepository;

    @Test
    public void saveTest() {
        AuthEventDto eventDto = new AuthEventDto("ip", OffsetDateTime.now(), "user", "type");

        authEventRepository.save(new AuthEventEntity(eventDto));

        List<AuthEventEntity> eventEntities = authEventRepository.findAll();
        assertEquals(1, eventEntities.size());

        AuthEventEntity eventEntity = eventEntities.get(0);
        assertNotNull(eventEntity.getId());
        assertEquals(eventDto.ipAddress(), eventEntity.getIpAddress());
        assertEquals(eventDto.time(), eventEntity.getEventTime());
        assertEquals(eventDto.userName(), eventEntity.getUserName());
        assertEquals(eventDto.type(), eventEntity.getEventType());

        authEventRepository.save(new AuthEventEntity(new AuthEventDto("127.0.0.1", OffsetDateTime.now(), "root", "auth")));

        eventEntities = authEventRepository.findAll();
        assertEquals(2, eventEntities.size());

        assertTrue(eventEntities.contains(eventEntity));
    }

}