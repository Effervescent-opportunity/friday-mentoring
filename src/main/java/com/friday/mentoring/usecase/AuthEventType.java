package com.friday.mentoring.usecase;

import java.util.HashMap;
import java.util.Map;

public enum AuthEventType {
    AUTHENTICATION_SUCCESS("AUTHENTICATION_SUCCESS"),
    AUTHENTICATION_FAILURE("AUTHENTICATION_FAILURE"),
    AUTHORIZATION_FAILURE("AUTHORIZATION_FAILURE");

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
