package com.friday.mentoring.db.repository;

import com.friday.mentoring.BaseDatabaseIntegrationTest;
import com.friday.mentoring.db.entity.AuthEventEntity;
import com.friday.mentoring.dto.AuthEventDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)//todo why it's working only with this annotation?
class AuthEventRepositoryTest extends BaseDatabaseIntegrationTest {//todo write tests & for outbox

    @Autowired
    AuthEventRepository authEventRepository;

    @Test
    public void t1() {
        authEventRepository.save(new AuthEventEntity(new AuthEventDto("ipAddress", OffsetDateTime.now(), "user", "type")));

        assertEquals(1, authEventRepository.count());
    }

}