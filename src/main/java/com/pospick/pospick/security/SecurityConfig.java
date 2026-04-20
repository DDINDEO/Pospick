package com.pospick.pospick.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정
 * 3주차 전까지 전체 허용 상태 유지
 * TODO: 3주차 - JWT 필터 등록 및 ORGANIZER/SELLER 권한별 접근 제어 적용
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 비밀번호 암호화에 사용하는 BCrypt 인코더 빈 등록
     * 회원가입 시 비밀번호 암호화, 로그인 시 비밀번호 검증에 사용
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 3주차 전까지 전체 허용 — JWT 필터 및 권한 분리는 3주차에 적용
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
