package com.friday.mentoring.db.repository;

import com.friday.mentoring.BaseDatabaseIntegrationTest;
import com.friday.mentoring.configuration.CustomDbConfig;
import com.friday.mentoring.db.entity.OutboxEntity;
import com.friday.mentoring.dto.AuthEventDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.util.List;

import static java.time.OffsetDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class OutboxRepositoryTest extends BaseDatabaseIntegrationTest {//todo write tests for outbox

//I have to add com.friday.mentoring.configuration.CustomDbConfig to this beans and I don't know how todo this

    @Autowired
    OutboxRepository outboxRepository;

    @Test
    public void top10Test() {
        for (int i = 0; i < 11; i++) {
            outboxRepository.save(new OutboxEntity(new AuthEventDto("ip" + i, now(), "user", "type")));
        }

        List<OutboxEntity> entities = outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(0, now().minusMinutes(2L), OffsetDateTime.MAX);
        assertEquals(10, entities.size());
    }

    @Test
    public void findByRetryCountAndCreatedAtBetweenTest() {
        AuthEventDto eventDto1 = new AuthEventDto("ip1", now().minusMinutes(3L), "user", "type");
        AuthEventDto eventDto2 = new AuthEventDto("ip2", now().minusMinutes(3L), "user", "type");
        AuthEventDto eventDto3 = new AuthEventDto("ip3", now(), "user", "type");
        AuthEventDto eventDto4 = new AuthEventDto("ip4", now(), "user", "type");

        OutboxEntity entity2 = new OutboxEntity(eventDto2);
        OutboxEntity entity3 = new OutboxEntity(eventDto3);
        entity2.setRetryCount(0);
        entity3.setRetryCount(0);

        outboxRepository.save(new OutboxEntity(eventDto1));
        outboxRepository.save(entity2);
        outboxRepository.save(entity3);
        outboxRepository.save(new OutboxEntity(eventDto4));

        List<OutboxEntity> entities = outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(0, now().minusMinutes(1L), now().plusMinutes(1L));
        assertEquals(1, entities.size());
        assertEquals(eventDto4, entities.get(0).getEvent());

        entities = outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(-1, now().minusMinutes(1L), now().plusMinutes(1L));
        assertEquals(0, entities.size());

        entities = outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(10, now().minusMinutes(10L), now().minusMinutes(7L));
        assertEquals(0, entities.size());

        entities = outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(-1, now().minusMinutes(10L), now().plusMinutes(1L));
        assertEquals(4, entities.size());

        entities = outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(0, now().minusMinutes(5L), now().minusMinutes(2L));
        assertEquals(1, entities.size());
        assertEquals(eventDto1, entities.get(0).getEvent());
    }


}
