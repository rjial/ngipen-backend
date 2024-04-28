package com.rjial.ngipen.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Component
public class JWTUtils {

    private final SecretKey secretKey;

    private static final long EXPIRATION_TIME = 86400000;

    public JWTUtils(Environment env) {
        String secretString = env.getProperty("jwt.key");
        assert secretString != null;
        byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userdetails) {
        User user = (User) userdetails;
        return Jwts.builder()
                .subject(userdetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .claim("data", new UserClaim(user.getEmail(), user.getName(), user.getAddress(), user.getLevel().toString()))
                .compact();
    }

    public String generateRefreshToken(HashMap<String, Object> claims, UserDetails userdetails) {
        User user = (User) userdetails;
        return Jwts.builder()
                .claim("data", new UserClaim(user.getEmail(), user.getName(), user.getAddress(), user.getLevel().toString()))
                .subject(userdetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsFunction) {
        return claimsFunction.apply(Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }
}
