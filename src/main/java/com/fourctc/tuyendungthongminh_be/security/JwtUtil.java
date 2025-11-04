package com.fourctc.tuyendungthongminh_be.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "q8Jf4uGz9vYpLk2rT1xWm3nB7aC8dE5fF6gH7iJ8kL9mN0oP"; // Đổi thành key bảo mật của bạn

    public String generateToken(String subject, long expirationMillis) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}