package com.pospick.pospick.dto.request.auth;

/**
 * 회원가입 요청 DTO
 * Java Record — Jackson 3.x에서 별도 설정 없이 자동 역직렬화 지원
 */
public record SignupRequest(
        String loginId,
        String password,
        String name,
        String email,
        String role   // ORGANIZER, SELLER
) {}
