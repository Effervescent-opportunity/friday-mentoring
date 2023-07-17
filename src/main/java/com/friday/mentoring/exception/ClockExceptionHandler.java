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

/*

Без этой обработки возвращается HTTP 500, например:

➜  ~ http ":8080/time/current?timezone=Europe/Kyiw"
HTTP/1.1 500
Connection: close
Content-Type: application/json
Date: Fri, 15 Jul 2023 00:00:17 GMT
Transfer-Encoding: chunked

{
    "error": "Internal Server Error",
    "path": "/time/current",
    "status": 500,
    "timestamp": "2023-07-15T00:00:17.826+00:00"
}

Очень правильное решение, что HTTP 500 нам не подходит и нужно действительно использовать код ответа HTTP 400,
т.к. сервер не испытывает никаких внутренних проблем, это ошибка входных параметров от клиента.

Здесь есть несколько моментов:

1. Чтобы добавить кастомный обработчик, можно использовать новую фичу Spring Boot 3 - Problem Detail
(https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ProblemDetail.html).

В результате ответ будет таким:

➜  ~ http ":8080/time/current?timezone=Europe/Kyiw"
HTTP/1.1 400
Connection: close
Content-Type: application/problem+json
Date: Fri, 15 Jul 2023 00:00:30 GMT
Transfer-Encoding: chunked

{
    "detail": "Unknown time-zone ID: Europe/Kyiw",
    "instance": "/time/current",
    "status": 400,
    "title": "Bad Request"
}


2. ZoneRulesException обрабатывать не нужно, т.к. он наследуется от DateTimeException и следовательно
   обрабатывается автоматически.

*/
    @ExceptionHandler(value = DateTimeException.class)
    protected ProblemDetail handleTimezoneParsingErrors(DateTimeException ex, WebRequest request) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setDetail(ex.getMessage());
        pd.setType(URI.create(request.getContextPath()));
        return pd;
    }
}
