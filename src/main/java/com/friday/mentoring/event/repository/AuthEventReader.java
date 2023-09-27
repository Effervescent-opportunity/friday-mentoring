package com.friday.mentoring.event.repository;

import com.friday.mentoring.db.entity.AuthEventEntity;

import java.util.stream.Stream;

public interface AuthEventReader {//todo rename - this is awful

    Stream<AuthEventEntity> getNotSentEvents();//not get for sending because I know nothing about sending, I just get
    /*
    все я запуталась опять, кто там кого инкапсулирует и внутри\снаружи
    зато спустя 6,5 часов я поняла dependency inversion
     */
}
