package com.pospick.pospick.controller;

import com.pospick.pospick.dto.request.auth.LoginRequest;
import com.pospick.pospick.dto.request.auth.SignupRequest;
import com.pospick.pospick.dto.response.auth.LoginResponse;
import com.pospick.pospick.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 회원가입/로그인 API 컨트롤러
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     * POST /api/auth/signup
     * 요청 바디: { loginId, password, name, email, role }
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    /**
     * 로그인
     * POST /api/auth/login
     * 요청 바디: { loginId, password }
     * 응답: { userId, name, role, token }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
