package com.friday.mentoring.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

class AuthEventSpecifications {

    public static Specification<AuthEventEntity> userEquals(String user) {
        return new Specification<AuthEventEntity>() {
            @Override
            public Predicate toPredicate(Root<AuthEventEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get(AuthEventEntity_.USER_NAME), user);
//                return null;
            }
        };
    }

    public static Specification<AuthEventEntity> ipEquals(String ip) {
        return new Specification<AuthEventEntity>() {
            @Override
            public Predicate toPredicate(Root<AuthEventEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get(AuthEventEntity_.IP_ADDRESS), ip);
            }
        };
    }

    public static Specification<AuthEventEntity> typeEquals(String type) {
        return new Specification<AuthEventEntity>() {
            @Override
            public Predicate toPredicate(Root<AuthEventEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get(AuthEventEntity_.EVENT_TYPE), type);
            }
        };
    }

    public static Specification<AuthEventEntity> timeGreaterThan(OffsetDateTime startTime) {
        return new Specification<AuthEventEntity>() {
            @Override
            public Predicate toPredicate(Root<AuthEventEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(AuthEventEntity_.EVENT_TIME), startTime);
            }
        };
    }

    public static Specification<AuthEventEntity> timeLessThan(OffsetDateTime endTime) {
        return new Specification<AuthEventEntity>() {
            @Override
            public Predicate toPredicate(Root<AuthEventEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(AuthEventEntity_.EVENT_TIME), endTime);
            }
        };
    }
}
