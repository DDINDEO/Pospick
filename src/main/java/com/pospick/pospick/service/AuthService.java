package com.pospick.pospick.service;

import com.pospick.pospick.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;

    // TODO: 회원가입 - loginId 중복 확인 후 비밀번호 암호화하여 저장
    // TODO: 로그인 - loginId/password 검증 (3주차에 JWT 반환으로 업그레이드)
}
