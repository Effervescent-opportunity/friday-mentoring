package com.friday.mentoring.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.DateTimeException;
import java.time.zone.ZoneRulesException;


@ControllerAdvice
public class ClockExceptionHandler extends ResponseEntityExceptionHandler {

    //вот тут у меня так и не получилось сделать возврат строки в json, чтобы браузер не ругался
    @ExceptionHandler(value = {DateTimeException.class, ZoneRulesException.class})
    protected ResponseEntity<Object> handleTimezoneParsingErrors(RuntimeException ex, WebRequest request) {
        return ResponseEntity.badRequest().build();
    }
}
