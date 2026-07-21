package com.maven.spring.authservice.service;

import com.maven.spring.authservice.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Getter
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UserEntity user) {

        return buildToken(
                user,
                "ACCESS",
                accessTokenExpiration
        );
    }

    public String generateRefreshToken(UserEntity user) {

        return buildToken(
                user,
                "REFRESH",
                refreshTokenExpiration
        );
    }

    private String buildToken(
            UserEntity user,
            String tokenType,
            long expirationTime
    ) {

        Date issuedAt = new Date();

        Date expiresAt = new Date(
                issuedAt.getTime() + expirationTime
        );

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .claim("tokenType", tokenType)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractAllClaims(String token) {

        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (JwtException | IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid or expired token"
            );
        }
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isRefreshToken(String token) {

        String tokenType = extractAllClaims(token)
                .get("tokenType", String.class);

        return "REFRESH".equals(tokenType);
    }

}