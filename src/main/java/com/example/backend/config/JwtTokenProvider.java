package com.example.backend.config;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // Cached SecretKey for performance
    private SecretKey cachedKey;

    // Helper method to get or create signing key
    private SecretKey getSigningKey() {
        if (cachedKey == null) {
            cachedKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }
        return cachedKey;
    }

    // JWT token generation
    public String generateToken(Authentication authentication, String loginType) {
        System.out.println("????000");
        System.out.println("????111" + authentication.getName());

        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        System.out.println("3333 => " + jwtSecret);
        System.out.println("Secret Key Length: " + jwtSecret.length());

        return Jwts.builder()
                .subject(username)
                .claim("loginType", loginType)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    // Extract username from JWT token
    public String getUsernameFromJWT(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // JWT token validation
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            System.out.println("Invalid JWT token: " + ex.getMessage());
        }
        return false;
    }
}
