package com.friday.mentoring.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class AuthEventDto {

    private String ip;
    private ZonedDateTime time;
    private String login;
    private AuthStatusEnum status;


    //IPv4 адрес, время, логин, вид события

    private enum AuthStatusEnum {
        SUCCESS, FAIL
    }
}
