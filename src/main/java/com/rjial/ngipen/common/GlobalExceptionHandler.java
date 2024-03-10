package com.rjial.ngipen.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<Response> handleException(Exception ex) {
        Response res = new Response<>();
        res.setStatusCode(Long.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        res.setMessage(ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(res);
    }
}
