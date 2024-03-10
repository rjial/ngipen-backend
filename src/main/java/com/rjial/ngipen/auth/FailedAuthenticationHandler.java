package com.rjial.ngipen.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjial.ngipen.common.Response;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FailedAuthenticationHandler implements AccessDeniedHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // throw new BadCredentialsException(accessDeniedException.getMessage());
        Response res = new Response<>();
        res.setStatusCode(Long.valueOf(HttpStatus.UNAUTHORIZED.value()));
        res.setMessage(accessDeniedException.getMessage());
        response.setContentType("application/json");
        response.setStatus(res.getStatusCode().intValue());
        String errorJson = objectMapper.writeValueAsString(res);
        response.getWriter().write(errorJson);
    }

}
