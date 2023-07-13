package com.friday.mentoring.exception;

import com.friday.mentoring.controller.ClockController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.DateTimeException;
import java.time.zone.ZoneRulesException;

@ControllerAdvice
public class ClockExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClockController.class);

 //   @ResponseBody
    @ExceptionHandler(value = {DateTimeException.class, ZoneRulesException.class})
    protected ResponseEntity<String> handleTimezoneParsingErrors(RuntimeException ex, WebRequest request) {
        LOGGER.info("LALALA request [{}]", request.getParameterNames());
        //      HttpHeaders headers = new HttpHeaders();
        //      headers.setContentType(MediaType.APPLICATION_JSON);
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body("Incorrect timezone");

//        return handleExceptionInternal(ex, "Incorrect timezone", headers, HttpStatus.BAD_REQUEST, request);
    }
}
