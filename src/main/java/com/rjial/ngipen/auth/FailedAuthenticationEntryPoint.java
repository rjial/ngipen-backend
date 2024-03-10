package com.rjial.ngipen.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rjial.ngipen.common.Response;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        // throw new BadCredentialsException(authException.getMessage());
        response.setContentType("application/json");
        Response res = new Response<>();
        res.setStatusCode(Long.valueOf(HttpStatus.UNAUTHORIZED.value()));
        response.setStatus(res.getStatusCode().intValue()); 
        res.setMessage(authException.getMessage());
        String errorJson = objectMapper.writeValueAsString(res);
        response.getWriter().write(errorJson);
    }

}
