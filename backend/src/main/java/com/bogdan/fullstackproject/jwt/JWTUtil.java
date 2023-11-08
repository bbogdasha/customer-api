package com.bogdan.fullstackproject.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.util.Map;

import static java.time.temporal.ChronoUnit.MILLIS;

@Component
public class JWTUtil {

    @Value("${jwt.token.secret}")
    private String SECRET_KEY;

    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 hour

    public String generateAccessToken(String subject) {
        return generateAccessToken(subject, Map.of());
    }

    public String generateAccessToken(String subject, String ...scopes) {
        return generateAccessToken(subject, Map.of("scopes", scopes));
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer("bogdash")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(EXPIRE_DURATION, MILLIS)))
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
