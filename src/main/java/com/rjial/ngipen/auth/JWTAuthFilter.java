package com.rjial.ngipen.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JWTAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String userEmail;
        log.info(authHeader);
        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        token = authHeader.substring(7);
        log.info(token);
        userEmail = jwtUtils.extractUsername(token);
        log.info(userEmail);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (jwtUtils.isTokenValid(token, userDetails)) {
                log.info(userDetails.getUsername());
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken tokenRes = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                tokenRes.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                securityContext.setAuthentication(tokenRes);
                SecurityContextHolder.setContext(securityContext);
                filterChain.doFilter(request, response);
                return;
            } else {
                throw new BadCredentialsException("Invalid Token");
            }
        } else {
            throw new BadCredentialsException("Invalid Token");
        }
    }
}
