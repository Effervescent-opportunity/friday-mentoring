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
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)//todo why it's working only with this annotation? b
    //because of this: By default, tests annotated with @DataJpaTest are transactional and roll back at the end of each test. They also use an embedded in-memory database (replacing any explicit or usually auto-configured DataSource). The @AutoConfigureTestDatabase annotation can be used to override these settings.
class AuthEventRepositoryTest extends BaseDatabaseIntegrationTest {

    @Autowired
    AuthEventRepository authEventRepository;

    @Test
    public void save() {
        AuthEventDto authEventDto = new AuthEventDto("ipAddress", OffsetDateTime.now(), "user", "type");

        authEventRepository.save(new AuthEventEntity(authEventDto));

        List<AuthEventEntity> authEventEntities = authEventRepository.findAll();
        assertEquals(1, authEventEntities.size());
        AuthEventEntity authEventEntity = authEventEntities.get(0);
        assertNotNull(authEventEntity.getId());
        assertEquals(authEventDto.ipAddress(), authEventEntity.getIpAddress());
        assertEquals(authEventDto.time(), authEventEntity.getEventTime());
        assertEquals(authEventDto.userName(), authEventEntity.getUserName());
        assertEquals(authEventDto.type(), authEventEntity.getEventType());

        authEventRepository.save(new AuthEventEntity(new AuthEventDto("127.0.0.1", OffsetDateTime.now(), "root", "auth")));

        authEventEntities = authEventRepository.findAll();
        assertEquals(2, authEventEntities.size());

        assertTrue(authEventEntities.contains(authEventEntity));
    }

}