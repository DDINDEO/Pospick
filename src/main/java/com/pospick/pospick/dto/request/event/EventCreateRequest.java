package com.pospick.pospick.dto.request.event;

import java.time.LocalDate;

/**
 * 행사 생성 요청 DTO
 * Java Record — Jackson 3.x에서 별도 설정 없이 자동 역직렬화 지원
 * TODO: organizerId 제거하고 JWT 토큰에서 자동 추출로 변경 필요
 *
 * collectSales: 매출 데이터 수집 여부 (true면 판매자 매출 정보 수집)
 * collectStock: 재고 데이터 수집 여부 (true면 판매자 재고 정보 수집)
 */
public record EventCreateRequest(
        Long organizerId,
        String title,
        String description,
        String location,
        LocalDate startDate,
        LocalDate endDate,
        String posterUrl,
        boolean collectSales,  // 매출 수집 여부
        boolean collectStock   // 재고 수집 여부
) {}
