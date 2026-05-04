package com.example.SmartShop.AI.Assistant.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class JwtService {

    private final Key key;
    private final long expirationTime;

    public JwtService(
            @Value("${jwt.secret-key}") String secret,
            @Value("${jwt.expiration-ms:86400000}") long expirationTime
    ) {

        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 characters long");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
    }

    // =========================
    // TOKEN GENERATION
    // =========================
    public String generateToken(String email) {
        return generateToken(email, Map.of());
    }

    public String generateToken(String email, Map<String, Object> extraClaims) {

        log.debug("[JwtService] Generating JWT for {}", email);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    // =========================
    // EXTRACTION
    // =========================
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("[JwtService] Invalid JWT token: {}", ex.getMessage());
            return false;
        }
    }

    // =========================
    // INTERNAL
    // =========================
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}