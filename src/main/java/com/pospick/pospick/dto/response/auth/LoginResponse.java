package com.pospick.pospick.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 응답 DTO
 * 로그인 성공 시 JWT 토큰과 유저 정보 반환
 * 이후 모든 API 요청 시 Authorization 헤더에 토큰을 담아서 보내야 함
 * 형식: Authorization: Bearer {token}
 */
@Getter
@AllArgsConstructor
public class LoginResponse {
    private Long userId;   // 유저 ID
    private String name;   // 이름
    private String role;   // 역할 (ORGANIZER / SELLER)
    private String token;  // JWT 토큰
}
