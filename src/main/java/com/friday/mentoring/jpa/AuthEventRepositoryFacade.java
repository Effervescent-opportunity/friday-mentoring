package com.friday.mentoring.jpa;

import com.friday.mentoring.usecase.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;

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
    public Page<AuthEventEntity> getFilteredEntities(String user, String ip, String type, OffsetDateTime dateFrom,
                                                     OffsetDateTime dateTo, int page, int size, String[] sort) {
        //validation part

        if (size <0 || size > 100)  {
            throw new RuntimeException();//todo return http 400 + add field validation
        }
        Specification<AuthEventEntity> spec;
        Sort sortObject;
        Pageable pageable;
//       return authEventRepository.findAll(spec, pageable);
        return null;//try catch while making sort?
    }
}
