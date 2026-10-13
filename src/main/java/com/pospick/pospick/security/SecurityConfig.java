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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
     * CORS 설정
     * - React 개발 서버(localhost:5173)에서 오는 요청 허용
     * - Authorization 헤더(JWT 토큰) 허용
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // React 개발 서버
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*")); // 모든 헤더 허용 (Authorization 포함)
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 경로에 적용
        return source;
    }

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
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            // JWT 사용 → 세션 사용 안 함
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 회원가입 / 로그인은 누구나 접근 가능
                .requestMatchers("/api/auth/**").permitAll()
                // 행사 목록/단건 조회는 GET만 누구나 가능
                // /api/events/my는 아래에서 ORGANIZER 전용으로 처리되므로 제외
                .requestMatchers(HttpMethod.GET, "/api/events", "/api/events/{eventId}").permitAll()
                // 내 행사 목록은 ORGANIZER만 (로그인 필요)
                .requestMatchers(HttpMethod.GET, "/api/events/my").hasRole("ORGANIZER")
                // 행사 생성/수정/삭제(POST/PUT/DELETE)는 ORGANIZER만
                .requestMatchers("/api/events/**").hasRole("ORGANIZER")
                // 참가 신청은 ORGANIZER / SELLER 모두 가능
                .requestMatchers("/api/participations", "/api/participations/**").hasAnyRole("ORGANIZER", "SELLER")
                // 상품/주문은 SELLER만
                .requestMatchers("/api/products/**").hasRole("SELLER")
                .requestMatchers("/api/orders/**").hasRole("SELLER")
                // 통계: 행사 통계는 ORGANIZER, 부스 통계는 SELLER
                .requestMatchers("/api/stats/event/**").hasRole("ORGANIZER")
                .requestMatchers("/api/stats/booth/**").hasRole("SELLER")
                // 나머지는 로그인 필요
                .anyRequest().authenticated()
            )
            // JWT 필터를 Spring Security 기본 로그인 필터 앞에 끼워 넣음
            // → 요청이 들어오면 JWT 검증을 먼저 하고, 그 다음 권한 체크로 넘어감
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
