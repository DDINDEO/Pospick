package com.pospick.pospick.dto.request.auth;

import lombok.Getter;

/**
 * 회원가입 요청 DTO
 * 클라이언트가 POST /api/auth/signup 호출 시 보내는 데이터
 */
@Getter
public class SignupRequest {
    private String loginId;  // 로그인 아이디 (중복 불가)
    private String password; // 비밀번호 (서버에서 암호화 저장)
    private String name;     // 이름
    private String email;    // 이메일
    private String role;     // 역할: ORGANIZER(주최자) 또는 SELLER(판매자)
}
