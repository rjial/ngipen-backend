package com.rjial.ngipen.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;

import com.rjial.ngipen.common.Response;

@RestControllerAdvice
public class BadCredentialsExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class, AccessDeniedException.class})
    public ResponseEntity<Response> handleBadCredentialException(Exception exc) {
        exc.printStackTrace();
        Response res = new Response<>();
        res.setStatusCode(Long.valueOf(HttpStatus.BAD_REQUEST.value()));
        res.setMessage(exc.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

}
