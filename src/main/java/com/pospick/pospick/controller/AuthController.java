package com.pospick.pospick.controller;

import com.pospick.pospick.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // TODO: POST /api/auth/signup - 회원가입
    // TODO: POST /api/auth/login  - 로그인
}
