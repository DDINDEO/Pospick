package com.pospick.pospick.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 인증 필터
 * 모든 요청마다 Authorization 헤더에서 JWT 토큰을 꺼내 검증
 * 유효한 토큰이면 SecurityContext에 인증 정보 저장 → 이후 권한 체크에 사용
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Authorization 헤더에서 토큰 추출 (형식: "Bearer {토큰}")
        String header = request.getHeader("Authorization");

        // 2. 토큰 없으면 401 반환 (인증 안 됨)
        // 단, 회원가입/로그인/행사조회(GET)는 토큰 없어도 허용
        String path = request.getRequestURI();
        String method = request.getMethod();
        boolean isPublic = path.startsWith("/api/auth/")
                || (method.equals("GET") && path.startsWith("/api/events"));

        if (!isPublic && (header == null || !header.startsWith("Bearer "))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"status\": 401, \"message\": \"토큰이 없습니다.\"}");
            return;
        }

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // "Bearer " 제거

            // 3. 토큰 유효성 검증
            if (jwtTokenProvider.isValid(token)) {
                String loginId = jwtTokenProvider.getLoginId(token);
                String role = jwtTokenProvider.getRole(token);

                // 4. 인증 정보 생성 후 SecurityContext에 저장
                // UsernamePasswordAuthenticationToken(사용자식별자, 비밀번호, 권한목록)
                // - 비밀번호는 JWT 방식에서 불필요하므로 null
                // - Spring Security는 권한 앞에 "ROLE_" 붙는 걸 기본 규칙으로 사용
                //   예: SELLER → ROLE_SELLER, SecurityConfig의 hasRole("SELLER")와 매칭됨
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                loginId,                                          // principal (누구인지)
                                null,                                             // credentials (비밀번호, JWT에선 불필요)
                                List.of(new SimpleGrantedAuthority("ROLE_" + role)) // 권한
                        );
                // SecurityContextHolder = 현재 요청의 인증 정보를 저장하는 공간
                // 이후 Controller에서 @AuthenticationPrincipal로 loginId 꺼낼 수 있음
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                // 토큰이 있는데 유효하지 않은 경우 (만료/변조)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"status\": 401, \"message\": \"유효하지 않은 토큰입니다.\"}");
                return;
            }
        }

        // 5. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}
