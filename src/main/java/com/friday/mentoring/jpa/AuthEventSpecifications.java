package com.friday.mentoring.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

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
}
