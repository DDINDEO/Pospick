package com.pospick.pospick.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정
 * - JWT 필터 등록
 * - URL별 접근 권한 설정 (ORGANIZER / SELLER)
 * - 세션 사용 안 함 (JWT 기반 Stateless)
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 비밀번호 암호화에 사용하는 BCrypt 인코더 빈 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // JWT 사용 → 세션 사용 안 함
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 회원가입 / 로그인은 누구나 접근 가능
                .requestMatchers("/api/auth/**").permitAll()
                // 행사 목록/단건 조회는 GET만 누구나 가능 (HTTP 메서드 구분 필수!)
                .requestMatchers(HttpMethod.GET, "/api/events", "/api/events/**").permitAll()
                // 행사 생성/수정/삭제(POST/PUT/DELETE)는 ORGANIZER만
                .requestMatchers("/api/events/**").hasRole("ORGANIZER")
                // 참가 신청은 SELLER만
                .requestMatchers("/api/participations/**").hasAnyRole("ORGANIZER", "SELLER")
                // 상품/주문은 SELLER만
                .requestMatchers("/api/products/**").hasRole("SELLER")
                .requestMatchers("/api/orders/**").hasRole("SELLER")
                // 나머지는 로그인 필요
                .anyRequest().authenticated()
            )
            // JWT 필터를 Spring Security 필터 앞에 등록
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
