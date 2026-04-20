package com.zentra.server.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utility class for generating and parsing JWT tokens
 */
public class JwtUtil {

    private static final String SECRET_KEY = "zentra-secret-key-1234567890123456";

    /**
     * Generate JWT token using user ID
     *
     * @param userId user ID
     * @return JWT token string
     */
    public static String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 hour
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }



    /**
     * Parse JWT token and return claims
     *
     * @param token JWT token
     * @return Claims object containing token data
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
