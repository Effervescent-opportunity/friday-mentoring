package com.friday.mentoring.jpa;

import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

class AuthEventSpecifications {

    public static Specification<AuthEventEntity> userEquals(String user) {
        return (root, query, criteriaBuilder) -> {
            if (user == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get(AuthEventEntity_.USER_NAME), user);
        };
    }

    public static Specification<AuthEventEntity> ipEquals(String ip) {
        return (root, query, criteriaBuilder) -> {
            if (ip == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get(AuthEventEntity_.IP_ADDRESS), ip);
        };
    }

    public static Specification<AuthEventEntity> typeEquals(String type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get(AuthEventEntity_.EVENT_TYPE), type);
        };
    }

    public static Specification<AuthEventEntity> timeGreaterThan(OffsetDateTime startTime) {
        return (root, query, criteriaBuilder) -> {
            if (startTime == null) {
                return null;
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get(AuthEventEntity_.EVENT_TIME), startTime);
        };
    }

    public static Specification<AuthEventEntity> timeLessThan(OffsetDateTime endTime) {
        return (root, query, criteriaBuilder) -> {
            if (endTime == null) {
                return null;
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get(AuthEventEntity_.EVENT_TIME), endTime);
        };
    }
}
