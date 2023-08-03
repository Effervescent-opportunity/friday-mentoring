package com.friday.mentoring.dto;

import java.time.LocalDateTime;

public record AuthEventDto (String ipAddress, LocalDateTime time, String userName, String type) {//looks very similar to AudiEvent

//    private String ipAddress;
//    private LocalDateTime time;
//    private String userName;
//    private String type;
//
//    public AuthEventDto(String ipAddress, LocalDateTime time, String userName, String type) {
//        this.ipAddress = ipAddress;
//        this.time = time;
//        this.userName = userName;
//        this.type = type;
//    }
//


//IPv4 адрес, время, логин, вид события


}
