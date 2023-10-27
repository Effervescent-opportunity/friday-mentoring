package com.friday.mentoring.jpa;

import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

/**
 * Спецификации для фильтрации AuthEventEntity
 */
class AuthEventSpecifications {

    public static Specification<AuthEventEntity> userNameEquals(String userName) {
        return (root, query, criteriaBuilder) -> {
            if (userName == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get(AuthEventEntity_.USER_NAME), userName);
        };
    }

    public static Specification<AuthEventEntity> ipAddressEquals(String ipAddress) {
        return (root, query, criteriaBuilder) -> {
            if (ipAddress == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get(AuthEventEntity_.IP_ADDRESS), ipAddress);
        };
    }

    public static Specification<AuthEventEntity> eventTypeEquals(String eventType) {
        return (root, query, criteriaBuilder) -> {
            if (eventType == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get(AuthEventEntity_.EVENT_TYPE), eventType);
        };
    }

    public static Specification<AuthEventEntity> eventTimeGreaterThanOrEquals(OffsetDateTime eventTimeFrom) {
        return (root, query, criteriaBuilder) -> {
            if (eventTimeFrom == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get(AuthEventEntity_.EVENT_TIME), eventTimeFrom);
        };
    }

    public static Specification<AuthEventEntity> eventTimeLessThanOrEquals(OffsetDateTime eventTimeTo) {
        return (root, query, criteriaBuilder) -> {
            if (eventTimeTo == null) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get(AuthEventEntity_.EVENT_TIME), eventTimeTo);
        };
    }
}
