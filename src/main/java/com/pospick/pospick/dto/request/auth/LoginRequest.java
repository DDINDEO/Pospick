package com.pospick.pospick.dto.request.auth;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String loginId;
    private String password;
}
