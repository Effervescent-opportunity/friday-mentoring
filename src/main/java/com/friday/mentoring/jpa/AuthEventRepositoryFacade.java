package com.friday.mentoring.jpa;

import com.friday.mentoring.usecase.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.friday.mentoring.jpa.AuthEventEntity.*;
import static com.friday.mentoring.jpa.AuthEventEntity_.*;

@Component
class AuthEventRepositoryFacade implements EventRepository {

    private static final String SORT_DELIMITER = ",";
    /**
     * Названия полей, по которым возможна сортировка
     */
    private static final List<String> sortedFields = List.of(USER_NAME, IP_ADDRESS, EVENT_TYPE, EVENT_TIME);

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
    public Page<AuthEventEntity> getFilteredEntities(String userName, String ipAddress, String eventType, OffsetDateTime eventTimeFrom,
                                                     OffsetDateTime eventTimeTo, int page, int size, String[] sort) {
        if (page < 0) {
            throw new IllegalArgumentException("Номер страницы должен быть неотрицательным");
        }

        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Допустимый размер страницы: от 1 до 100 включительно");
        }

        if (eventTimeFrom != null && eventTimeTo != null && eventTimeFrom.isAfter(eventTimeTo)) {
            throw new IllegalArgumentException("Дата начала периода должна быть до даты окончания периода");
        }

        Specification<AuthEventEntity> spec = userNameEquals(userName)
                .and(ipAddressEquals(ipAddress))
                .and(eventTypeEquals(eventType))
                .and(eventTimeGreaterThanOrEquals(eventTimeFrom))
                .and(eventTimeLessThanOrEquals(eventTimeTo));

        Sort sortObject = getSortFromArray(sort);
        return authEventRepository.findAll(spec, PageRequest.of(page, size, sortObject));
    }

    private Sort getSortFromArray(String[] sort) {
        if (sort.length <= 1) {
            throw new IllegalArgumentException("Сортировка задана некорректно");
        }

        List<Order> sortOrders = new ArrayList<>();

        if (sort[0].contains(SORT_DELIMITER)) {
            // Сортировка по двум и более полям, массив вида: ["userName,desc", "eventType,asc"]
            for (String sortOrder : sort) {
                String[] fieldAndDirection = sortOrder.split(SORT_DELIMITER);
                if (fieldAndDirection.length != 2) {
                    throw new IllegalArgumentException("Сортировка задана некорректно");
                }
                sortOrders.add(getSortOrder(fieldAndDirection[0], fieldAndDirection[1]));
            }
        } else {
            // Сортировка по одному полю, массив вида: ["userName", "desc"]
            sortOrders.add(getSortOrder(sort[0], sort[1]));
        }

        return Sort.by(sortOrders);
    }

    private Order getSortOrder(String fieldName, String direction) {
        if (!sortedFields.contains(fieldName)) {
            throw new IllegalArgumentException("Некорректное название поля для сортировки: " + fieldName);
        }

        if (Direction.ASC.name().equalsIgnoreCase(direction)) {
            return new Order(Direction.ASC, fieldName);
        } else if (Direction.DESC.name().equalsIgnoreCase(direction)) {
            return new Order(Direction.DESC, fieldName);
        } else {
            throw new IllegalArgumentException("Некорректное направление сортировки: " + direction);
        }
    }
}
