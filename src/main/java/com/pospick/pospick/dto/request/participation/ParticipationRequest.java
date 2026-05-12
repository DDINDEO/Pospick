package com.pospick.pospick.dto.request.participation;

/**
 * 행사 참가 신청 요청 DTO
 *
 * eventId: 신청할 행사 ID
 * collectSalesAgree: 매출 데이터 수집 동의 여부 (행사에서 collectSales=true일 때 동의해야 함)
 * collectStockAgree: 재고 데이터 수집 동의 여부 (행사에서 collectStock=true일 때 동의해야 함)
 */
public record ParticipationRequest(
        Long eventId,
        boolean collectSalesAgree,  // 매출 수집 동의
        boolean collectStockAgree   // 재고 수집 동의
) {}
