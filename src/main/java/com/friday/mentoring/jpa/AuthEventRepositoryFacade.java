package com.friday.mentoring.jpa;

import com.friday.mentoring.usecase.EventRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.friday.mentoring.jpa.AuthEventSpecifications.*;

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
//todo ГЛАВНОЕ сейчас оно скорее всего взорвется - запустить и протестировать секьюрити, сваггер и как сейчас работают запросы в критерии
        if (page < 0) {
            throw new RuntimeException("incorrect page");//todo
        }

        if (size <= 0 || size > 100)  {
            throw new RuntimeException("incorrect size");//todo return http 400 + add field validation
        }

        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new RuntimeException("incorrect date");//todo
        }
//["user", "desc"] String[2]//["user,desc", "type,asc"] - String[2]//["user,desc", "type,asc", "ab,abs"] - String[3]
//        if (sort.length % 2 != 0) {//incorrect
//            throw new RuntimeException();//todo http 400
//        }

        Specification<AuthEventEntity> spec = Specification.where(null);

        if (user != null) {//todo try can I don't check if user is null
            spec = spec.and(userEquals(user));
        }

        if (ip != null) {
            spec = spec.and(ipEquals(ip));
        }

        if (type != null) {
            spec = spec.and(typeEquals(type));
        }

        if (dateFrom != null) {
            spec = spec.and(timeGreaterThan(dateFrom));
        }

        if (dateTo != null) {
            spec = spec.and(timeLessThan(dateTo));
        }

        Sort sortObject = getSortFromArray(sort);
        Pageable pageable = PageRequest.of(page, size, sortObject);
       return authEventRepository.findAll(spec, pageable);
       // return null;//todo try catch while making sort?
    }

    private Sort getSortFromArray(String[] sort) {
        List<Order> orders = new ArrayList<Order>();//todo wtf? help!

        if (sort[0].contains(",")) {
            // will sort more than 2 fields
            // sortOrder="field, direction"
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            // sort=[field, direction]
            orders.add(new Order(getSortDirection(sort[1]), sort[0]));
        }

        return Sort.unsorted();//todo
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }
}
