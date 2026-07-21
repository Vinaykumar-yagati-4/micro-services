package com.maven.spring.apigateway.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /*
     * Convert the Base64 secret from application.yaml
     * into a SecretKey used to verify the JWT signature.
     */
    private SecretKey getSigningKey() {

        byte[] keyBytes =
                Decoders.BASE64.decode(jwtSecret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    /*
     * Read and verify all claims from the token.
     */
    public Claims extractAllClaims(String token) {

        try {

            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (JwtException | IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "Invalid or expired access token"
            );
        }
    }

    /*
     * Validate:
     * 1. Signature
     * 2. Expiration
     * 3. Token type must be ACCESS
     */
    public boolean isValidAccessToken(String token) {

        Claims claims = extractAllClaims(token);

        String tokenType =
                claims.get("tokenType", String.class);

        Date expiration =
                claims.getExpiration();

        boolean accessToken =
                "ACCESS".equals(tokenType);

        boolean notExpired =
                expiration != null
                        && expiration.after(new Date());

        return accessToken && notExpired;
    }

    public String extractEmail(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    public String extractRole(String token) {

        return extractAllClaims(token)
                .get("role", String.class);
    }

    public Long extractUserId(String token) {

        Number userId = extractAllClaims(token)
                .get("userId", Number.class);

        if (userId == null) {
            return null;
        }

        return userId.longValue();
    }
}