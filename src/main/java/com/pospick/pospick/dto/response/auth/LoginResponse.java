package com.pospick.pospick.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token; // 3주차 JWT 도입 후 채울 예정
    private String role;
}
