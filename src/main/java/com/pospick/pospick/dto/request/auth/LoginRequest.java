package com.pospick.pospick.dto.request.auth;

/**
 * 로그인 요청 DTO
 * Java Record — Jackson 3.x에서 별도 설정 없이 자동 역직렬화 지원
 */
public record LoginRequest(
        String loginId,
        String password
) {}
