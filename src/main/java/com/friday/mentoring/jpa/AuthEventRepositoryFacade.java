package com.friday.mentoring.jpa;

import com.friday.mentoring.usecase.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static com.friday.mentoring.jpa.AuthEventEntity.*;

@Component
class AuthEventRepositoryFacade implements EventRepository {

    private final AuthEventRepository authEventRepository;

    public AuthEventRepositoryFacade(AuthEventRepository authEventRepository) {
        this.authEventRepository = authEventRepository;
    }

    @Override
    public void save(String ipAddress, OffsetDateTime time, String userName, String type) {
        authEventRepository.save(new AuthEventEntity(ipAddress, time, userName, type));
    }

    @Override
    public void setSuccessStatus(UUID id) {
        authEventRepository.setSuccessSentStatus(id);
    }

    @Override
    public Stream<AuthEventEntity> getNotSentEvents() {
        return authEventRepository.streamByWasSentFalse();
    }

    @Override
    public Page<EventRepository.AuthEventDto> getFilteredEntities(String userName, String ipAddress, String eventType, OffsetDateTime eventTimeFrom,
                                                                  OffsetDateTime eventTimeTo, Pageable pageable) {
        if (eventTimeFrom != null && eventTimeTo != null && eventTimeFrom.isAfter(eventTimeTo)) {
            throw new IllegalArgumentException("EventTimeFrom must be less than eventTimeTo");
        }

        Specification<AuthEventEntity> spec = userNameEquals(userName)
                .and(ipAddressEquals(ipAddress))
                .and(eventTypeEquals(eventType))
                .and(eventTimeGreaterThanOrEquals(eventTimeFrom))
                .and(eventTimeLessThanOrEquals(eventTimeTo));

        return authEventRepository.findAll(spec, pageable)
                .map(entity -> new AuthEventDto(entity.getIpAddress(), entity.getEventTime(), entity.getUserName(), entity.getEventType()));
    }

}
