package com.friday.mentoring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.DateTimeException;


@ControllerAdvice
public class ClockExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = DateTimeException.class)
    protected ProblemDetail handleTimezoneParsingErrors(DateTimeException ex, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setDetail(ex.getMessage());
        pd.setType(URI.create(request.getContextPath()));
        return pd;
    }
}
