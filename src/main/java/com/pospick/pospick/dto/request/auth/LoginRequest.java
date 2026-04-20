package com.pospick.pospick.dto.request.auth;

import lombok.Getter;

/**
 * 로그인 요청 DTO
 * 클라이언트가 POST /api/auth/login 호출 시 보내는 데이터
 */
@Getter
public class LoginRequest {
    private String loginId;  // 로그인 아이디
    private String password; // 비밀번호
}
