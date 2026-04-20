package com.pospick.pospick.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 응답 DTO
 * 로그인 성공 시 클라이언트에게 반환하는 데이터
 * TODO: 3주차 JWT 도입 후 token 필드 채울 예정
 */
@Getter
@AllArgsConstructor
public class LoginResponse {
    private Long userId;   // 유저 ID
    private String name;   // 이름
    private String role;   // 역할 (ORGANIZER / SELLER)
    private String token;  // TODO: 3주차 JWT 토큰으로 채울 예정 (현재 null)
}
