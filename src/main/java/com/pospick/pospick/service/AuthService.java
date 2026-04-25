package com.pospick.pospick.service;

import com.pospick.pospick.domain.User;
import com.pospick.pospick.dto.request.auth.LoginRequest;
import com.pospick.pospick.dto.request.auth.SignupRequest;
import com.pospick.pospick.dto.response.auth.LoginResponse;
import com.pospick.pospick.exception.CustomException;
import com.pospick.pospick.repository.UserRepository;
import com.pospick.pospick.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원가입/로그인 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     * - loginId 중복 확인
     * - 비밀번호 BCrypt 암호화 후 저장
     */
    @Transactional
    public void signup(SignupRequest request) {
        // loginId 중복 확인
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다.");
        }

        // 유저 엔티티 생성 및 비밀번호 암호화
        User user = new User();
        user.setLoginId(request.loginId());
        user.setPassword(passwordEncoder.encode(request.password())); // 암호화
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());

        userRepository.save(user);
    }

    /**
     * 로그인
     * - loginId/password 검증
     * - JWT 토큰 발급하여 반환
     */
    public LoginResponse login(LoginRequest request) {
        // loginId로 유저 조회 (없으면 401 에러)
        User user = userRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // JWT 토큰 발급
        String token = jwtTokenProvider.generateToken(user.getLoginId(), user.getRole());

        return new LoginResponse(user.getUserId(), user.getName(), user.getRole(), token);
    }
}
