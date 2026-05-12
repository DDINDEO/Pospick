package com.pospick.pospick.dto.response.participation;

import com.pospick.pospick.domain.Participation;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 참가 신청 응답 DTO
 * - partId: 참가 신청 ID
 * - eventId: 행사 ID
 * - sellerName: 신청한 판매자 이름
 * - status: 신청 상태 (PENDING / APPROVED / REJECTED)
 * - collectSalesAgree: 매출 수집 동의 여부
 * - collectStockAgree: 재고 수집 동의 여부
 * - agreedAt: 동의한 시각
 */
@Getter
public class ParticipationResponse {

    private final Long partId;
    private final Long eventId;
    private final String sellerName;
    private final String status;
    private final boolean collectSalesAgree;
    private final boolean collectStockAgree;
    private final LocalDateTime agreedAt;

    /**
     * Participation 엔티티로부터 응답 DTO 생성
     */
    public ParticipationResponse(Participation participation) {
        this.partId = participation.getPartId();
        this.eventId = participation.getEvent().getEventId();
        this.sellerName = participation.getSeller().getName();
        this.status = participation.getStatus();
        this.collectSalesAgree = participation.isCollectSalesAgree();
        this.collectStockAgree = participation.isCollectStockAgree();
        this.agreedAt = participation.getAgreedAt();
    }
}
