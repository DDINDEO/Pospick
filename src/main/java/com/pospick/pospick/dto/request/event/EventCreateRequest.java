package com.pospick.pospick.dto.request.event;

import java.time.LocalDate;

/**
 * 행사 생성 요청 DTO
 * Java Record — Jackson 3.x에서 별도 설정 없이 자동 역직렬화 지원
 * TODO: 3주차 JWT 도입 후 organizerId 제거하고 토큰에서 자동 추출
 */
public record EventCreateRequest(
        Long organizerId,
        String title,
        String description,
        String location,
        LocalDate startDate,
        LocalDate endDate,
        String posterUrl
) {}
