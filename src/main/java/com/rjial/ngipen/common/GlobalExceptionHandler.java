package com.rjial.ngipen.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class, RuntimeException.class, NotFoundException.class, NoSuchElementException.class, BadRequestException.class})
    public ResponseEntity<Response> handleException(Exception ex) {
        Response res = new Response<>();
        ex.printStackTrace();
        log.error(ex.getMessage());
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof NotFoundException || ex instanceof NoSuchElementException) {
            status = HttpStatus.NOT_FOUND;
        }
        if (ex instanceof BadRequestException) {
            status = HttpStatus.BAD_REQUEST;
        }
        res.setStatusCode(Long.valueOf(status.value()));
        res.setMessage(ex.getMessage());
        return ResponseEntity
            .status(status)
            .body(res);
    }
}
