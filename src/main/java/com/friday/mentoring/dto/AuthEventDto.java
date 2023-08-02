package com.friday.mentoring.dto;

import java.time.LocalDateTime;

public class AuthEventDto {//looks very similar to AudiEvent

    private String ip;
    private LocalDateTime time;
    private String userName;
    private AuthTypeEnum status;


    //IPv4 адрес, время, логин, вид события

    private enum AuthTypeEnum {
        SUCCESS, FAIL
    }
}
