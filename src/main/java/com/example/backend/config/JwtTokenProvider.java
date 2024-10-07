package com.example.backend.config;


import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    // JWT 생성 메서드
    public String generateToken(Authentication authentication, String loginType) {
        System.out.println("테스트000");
        System.out.println("테스트111" + authentication.getName());

        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        System.out.println("3333 => " + jwtSecret);
        System.out.println("Secret Key Length: " + jwtSecret.length());

        try {
            // 문자열을 바이트 배열로 변환하여 사용
            byte[] secretKeyBytes = jwtSecret.getBytes("UTF-8");

            return Jwts.builder()
                    .setSubject(username)
                    .claim("loginType", loginType)
                    .setIssuedAt(new Date())
                    .setExpiration(expiryDate)
                    .signWith(SignatureAlgorithm.HS512, secretKeyBytes)
                    .compact();
        } catch (UnsupportedEncodingException e) {
            // 예외 처리 (로그를 남기거나 예외를 던질 수 있음)
            throw new RuntimeException("UTF-8 인코딩을 지원하지 않습니다.", e);
        }

    }

    // JWT에서 사용자 이름 추출
    public String getUsernameFromJWT(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            System.out.println("Invalid JWT token");
        }
        return false;
    }
}
