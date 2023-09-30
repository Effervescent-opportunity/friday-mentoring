package com.friday.mentoring.event;

public enum AuthEventType {//todo maybe наоборот?
    AUTHN_SUCCESS("AUTHENTICATION_SUCCESS"),
    AUTHN_FAILURE("AUTHENTICATION_FAILURE"),
    AUTHZ_FAILURE("AUTHORIZATION_FAILURE");

    private String springName;

    AuthEventType(String springName) {

    }
}
