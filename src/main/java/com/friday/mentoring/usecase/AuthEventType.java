package com.friday.mentoring.usecase;

import java.util.HashMap;
import java.util.Map;

public enum AuthEventType {//todo maybe наоборот?//todo where move?
    //    AUTHENTICATION_SUCCESS,
//    AUTHENTICATION_FAILURE,
//    AUTHORIZATION_FAILURE,
    AUTHN_SUCCESS("AUTHENTICATION_SUCCESS"),
    AUTHN_FAILURE("AUTHENTICATION_FAILURE"),
    AUTHZ_FAILURE("AUTHORIZATION_FAILURE");

    private final String springName;

    private static final Map<String, AuthEventType> map;

    static {
        map = new HashMap<>();
        for (AuthEventType type : AuthEventType.values()) {
            map.put(type.springName, type);
        }
    }

    AuthEventType(String springName) {
        this.springName = springName;
    }

    public static AuthEventType getBySpringName(String springName) {
        return map.get(springName);
    }

    public String getSpringName() {
        return springName;
    }
}
