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

import static com.friday.mentoring.jpa.AuthEventEntity_.*;
import static com.friday.mentoring.jpa.AuthEventSpecifications.*;

@Component
class AuthEventRepositoryFacade implements EventRepository {

    private static final String SORT_DELIMITER = ",";

    private static final List<String> sortedFields;

    static {
        sortedFields = new ArrayList<>();
        sortedFields.add(ID);
        sortedFields.add(USER_NAME);
        sortedFields.add(IP_ADDRESS);
        sortedFields.add(EVENT_TYPE);
        sortedFields.add(EVENT_TIME);
    }

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
    public Page<AuthEventEntity> getFilteredEntities1(String user, String ip, String type, OffsetDateTime dateFrom,
                                                      OffsetDateTime dateTo, Pageable pageable) {
//        if (page < 0) {//todo is it reachable?
//            throw new IllegalArgumentException("Номер страницы должен быть неотрицательным");
//        }
//
//        if (size <= 0 || size > 100) {
//            throw new IllegalArgumentException("Допустимый размер страницы: от 0 до 100 включительно");
//        }

        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new IllegalArgumentException("Дата начала периода должна быть до даты окончания периода");
        }//todo sort order property

        Specification<AuthEventEntity> spec = userEquals(user).and(ipEquals(ip)).and(typeEquals(type))
                .and(timeGreaterThan(dateFrom)).and(timeLessThan(dateTo));

        return authEventRepository.findAll(spec, pageable);
    }

    @Override
    public Page<AuthEventEntity> getFilteredEntities(String user, String ip, String type, OffsetDateTime dateFrom,
                                                     OffsetDateTime dateTo, int page, int size, String[] sort) {
        if (page < 0) {
            throw new IllegalArgumentException("Номер страницы должен быть неотрицательным");
        }

        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Допустимый размер страницы: от 0 до 100 включительно");
        }

        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new IllegalArgumentException("Дата начала периода должна быть до даты окончания периода");
        }

        Specification<AuthEventEntity> spec = userEquals(user).and(ipEquals(ip)).and(typeEquals(type))
                .and(timeGreaterThan(dateFrom)).and(timeLessThan(dateTo));

        Sort sortObject = getSortFromArray(sort);
        return authEventRepository.findAll(spec, PageRequest.of(page, size, sortObject));
    }

    private Sort getSortFromArray(String[] sort) {
        if (sort.length <= 1) {//todo try without sort in controller - is it parsed to [id, desc] String[2]? Or to String[1]?//string 2
            throw new IllegalArgumentException("Некорректно задаincorrect sort");
        }

        //["user", "desc"] String[2]
        // ["user,desc", "type,asc"] - String[2]
        // ["user,desc", "type,asc", "ab,abs"] - String[3]
//        if (sort.length % 2 != 0) {//incorrect
//            throw new RuntimeException();//todo http 400
//        }


        List<Order> sortOrders = new ArrayList<>();

        if (sort[0].contains(SORT_DELIMITER)) {
            // will sort more than 2 fields//todo translate to Russian
            // sortOrder="field, direction"
            for (String sortOrder : sort) {
                String[] fieldAndDirection = sortOrder.split(SORT_DELIMITER);
                if (fieldAndDirection.length != 2) {
                    throw new IllegalArgumentException("Incorrect one sort");//todo rename
                }
                sortOrders.add(getSortOrder(fieldAndDirection[0], fieldAndDirection[1]));
            }
        } else {
            // sort=[field, direction]
            sortOrders.add(getSortOrder(sort[0], sort[1]));
        }

        return Sort.by(sortOrders);
    }

    private Order getSortOrder(String fieldName, String direction) {
        if (!sortedFields.contains(fieldName)) {
            throw new IllegalArgumentException("Некорректное название поля для сортировки: " + fieldName);
        }

        if (Sort.Direction.ASC.name().equalsIgnoreCase(direction)) {
            return new Order(Sort.Direction.ASC, fieldName);
        } else if (Sort.Direction.DESC.name().equalsIgnoreCase(direction)) {
            return new Order(Sort.Direction.DESC, fieldName);
        } else {
            throw new IllegalArgumentException("Некорректное направление сортировки: " + direction);
        }
    }
}
