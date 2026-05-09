package com.zentra.common.util;

import com.zentra.common.auth.AuthInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utility class for generating and parsing JWT tokens
 */
public class JwtUtil {

    /**
     * Token expiration time (7 days)
     */
    private static final long EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    /**
     * Secret key for signing JWT tokens
     */
    private static final String SECRET_KEY = "zentra-secret-key-1234567890123456";

    private static final SecretKey KEY = Keys.hmacShaKeyFor(
            SECRET_KEY.getBytes(StandardCharsets.UTF_8)
    );

    /**
     * Generate JWT token
     */
    public static String generateToken(AuthInfo authInfo) {

        return Jwts.builder()

                .claim("userId", authInfo.getUserId())
                .claim("merchantId", authInfo.getMerchantId())
                .claim("userType", authInfo.getUserType())
                .setExpiration(new Date(
                        System.currentTimeMillis() + EXPIRATION
                ))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Parse JWT token
     */
    public static AuthInfo parseToken(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long userId = ((Number) claims.get("userId")).longValue();

        Long merchantId = ((Number) claims.get("merchantId")).longValue();

        String userType = (String) claims.get("userType");

        return new AuthInfo(userId, merchantId, userType);
    }

    private JwtUtil() {
    }
}
