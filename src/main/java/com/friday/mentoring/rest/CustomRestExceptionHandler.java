package com.friday.mentoring.rest;

import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.DateTimeException;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = DateTimeException.class)
    protected ProblemDetail handleTimezoneParsingErrors(DateTimeException ex, WebRequest request) {
        return getBadRequestResult(ex.getMessage(), request);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    protected ProblemDetail handleIllegalArgumentErrors(IllegalArgumentException ex, WebRequest request) {
        return getBadRequestResult(ex.getMessage(), request);
    }

    @ExceptionHandler(value = PropertyReferenceException.class)
    protected ProblemDetail handleIllegalColumnNameErrors(PropertyReferenceException ex, WebRequest request) {
        return getBadRequestResult("Incorrect property name for sorting: " + ex.getPropertyName(), request);
    }

    private static ProblemDetail getBadRequestResult(String errorMessage, WebRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setDetail(errorMessage);
        pd.setType(URI.create(request.getContextPath()));
        return pd;
    }
}
