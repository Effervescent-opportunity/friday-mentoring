package com.friday.mentoring.db.repository;

import com.friday.mentoring.BaseDatabaseIntegrationTest;
import com.friday.mentoring.configuration.CustomDbConfig;
import com.friday.mentoring.db.entity.OutboxEntity;
import com.friday.mentoring.dto.AuthEventDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.OffsetDateTime;
import java.util.List;

import static java.time.OffsetDateTime.now;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JacksonAutoConfiguration.class, CustomDbConfig.class})
public class OutboxRepositoryTest extends BaseDatabaseIntegrationTest {

    @Autowired
    OutboxRepository outboxRepository;

    @Test
    public void findTop10Test() {
        for (int i = 0; i < 11; i++) {
            outboxRepository.save(new OutboxEntity(new AuthEventDto("ip" + i, now(), "user", "type")));
        }

        List<OutboxEntity> entities = outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(0, now().minusMinutes(2L), now().plusHours(1L));
        assertEquals(10, entities.size());
    }

    @Test
    public void findByRetryCountAndCreatedAtBetweenTest() {
        AuthEventDto eventDto1 = new AuthEventDto("ip1", now(), "user", "type");
        AuthEventDto eventDto2 = new AuthEventDto("ip2", now(), "user", "type");
        AuthEventDto eventDto3 = new AuthEventDto("ip3", now(), "user", "type");
        AuthEventDto eventDto4 = new AuthEventDto("ip4", now(), "user", "type");

        OutboxEntity entity1 = new OutboxEntity(eventDto1);
        OutboxEntity entity2 = new OutboxEntity(eventDto2);
        OutboxEntity entity3 = new OutboxEntity(eventDto3);
        entity1.setCreatedAt(now().minusMinutes(3L));
        entity2.setCreatedAt(now().minusMinutes(3L));
        entity2.setRetryCount(0);
        entity3.setRetryCount(0);

        outboxRepository.save(entity1);
        outboxRepository.save(entity2);
        outboxRepository.save(entity3);
        outboxRepository.save(new OutboxEntity(eventDto4));

        List<OutboxEntity> entities = outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(0, now().minusMinutes(1L), now().plusMinutes(1L));
        assertEquals(1, entities.size());
        assertEquals(eventDto4, entities.get(0).getEvent());

        entities = outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(-1, now().minusMinutes(10L), now().plusMinutes(1L));
        assertEquals(4, entities.size());

        entities = outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(-1, now().minusMinutes(20L), now().minusMinutes(10L));
        assertEquals(0, entities.size());

        entities = outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(-1, now().plusMinutes(1L), now().plusMinutes(2L));
        assertEquals(0, entities.size());

        entities = outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(10, now().minusMinutes(10L), now().plusMinutes(1L));
        assertEquals(0, entities.size());

        entities = outboxRepository.findTop10ByRetryCountGreaterThanAndCreatedAtBetween(0, now().minusMinutes(5L), now().minusMinutes(2L));
        assertEquals(1, entities.size());
        assertEquals(eventDto1, entities.get(0).getEvent());
    }

    @Test
    public void saveAndDeleteByIdTest() {
        AuthEventDto eventDto = new AuthEventDto("ip", OffsetDateTime.now(), "user", "type");

        outboxRepository.save(new OutboxEntity(eventDto));

        List<OutboxEntity> outboxEntities = outboxRepository.findAll();
        assertEquals(1, outboxEntities.size());
        OutboxEntity outboxEntity = outboxEntities.get(0);
        assertNotNull(outboxEntity.getId());
        assertEquals(5, outboxEntity.getRetryCount());
        assertTrue(now().isAfter(outboxEntity.getCreatedAt().minusMinutes(1L)));
        assertTrue(now().isBefore(outboxEntity.getCreatedAt().plusMinutes(1L)));
        assertEquals(eventDto, outboxEntity.getEvent());

        outboxRepository.save(new OutboxEntity(new AuthEventDto("127.0.0.1", OffsetDateTime.now(), "root", "auth")));

        outboxEntities = outboxRepository.findAll();
        assertEquals(2, outboxEntities.size());

        assertTrue(outboxEntities.contains(outboxEntity));

        outboxRepository.deleteById(outboxEntities.stream()
                .filter(entity -> !outboxEntity.getCreatedAt().isEqual(entity.getCreatedAt()))
                .map(OutboxEntity::getId)
                .findAny()
                .orElseThrow());

        outboxEntities = outboxRepository.findAll();
        assertEquals(1, outboxEntities.size());
        assertEquals(outboxEntity, outboxEntities.get(0));
    }

}
