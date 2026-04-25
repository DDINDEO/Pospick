package com.pospick.pospick.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증 유틸리티
 * - 로그인 시 토큰 생성
 * - 요청마다 토큰 유효성 검증
 * - 토큰에서 사용자 정보 추출
 */
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    /**
     * JWT 토큰 생성
     * @param loginId 로그인 아이디
     * @param role 역할 (ORGANIZER / SELLER)
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateToken(String loginId, String role) {
        return Jwts.builder()
                .subject(loginId)                          // 토큰 주인 (loginId)
                .claim("role", role)                       // 역할 정보 포함
                .issuedAt(new Date())                      // 발급 시간
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // 만료 시간
                .signWith(key)                             // 서명
                .compact();
    }

    /**
     * 토큰에서 loginId 추출
     */
    public String getLoginId(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 토큰에서 role 추출
     */
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * 토큰 유효성 검증
     * @return 유효하면 true, 만료/변조 등이면 false
     */
    public boolean isValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰을 파싱해서 Claims(내부 데이터) 추출
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
