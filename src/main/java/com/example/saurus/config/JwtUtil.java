package com.example.saurus.config;

import com.example.saurus.domain.common.exception.CustomException;
import com.example.saurus.domain.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final long TOKEN_TIME = 60 * 60 * 1000L; // 60분
    private static final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }


    public String createToken(Long userId, String email, String name, String phone, UserRole userRole) {
        Date date = new Date();

        if (userId == null) {
            throw new IllegalArgumentException("userId는 null일 수 없습니다");
        }

        log.info("Creating access token for userId: {}", userId);

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(userId))
                        .claim("email", email)
                        .claim("name", name)
                        .claim("phone", phone)
                        .claim("userRole", userRole.name())
                        .claim("tokenType", "access")
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }


    public String createRefreshToken(Long userId, String email, String name, String phone, UserRole userRole) {
        Date now = new Date();
        log.info("Creating refresh token for userId: {}", userId);

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(userId))
                        .claim("email", email)
                        .claim("name", name)
                        .claim("phone", phone)
                        .claim("userRole", userRole.name())
                        .claim("tokenType", "refresh")
                        .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_TIME))
                        .setIssuedAt(now)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Not Found Token");
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

