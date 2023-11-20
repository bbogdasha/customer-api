package com.bogdan.fullstackproject.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.time.Instant;
import java.util.List;
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

    public String generateAccessToken(String subject, List<String> scopes) {
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

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {

        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public boolean isTokenValid(String jwt, String username) {
        String subject = getSubject(jwt);
        return subject.equals(username) && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        Date today = Date.from(Instant.now());
        return getClaims(jwt).getExpiration().before(today);
    }
}
