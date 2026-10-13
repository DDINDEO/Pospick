package com.pospick.pospick.controller;

import com.pospick.pospick.dto.response.stats.BoothStatsResponse;
import com.pospick.pospick.dto.response.stats.EventStatsResponse;
import com.pospick.pospick.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 통계 API 컨트롤러
 * - ORGANIZER: 행사 전체 통계
 * - SELLER: 내 부스 통계
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    /**
     * 행사 전체 통계 (ORGANIZER용)
     * GET /api/stats/event/{eventId}
     * → 총 매출, 부스별 매출, 카테고리별 판매량
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<EventStatsResponse> getEventStats(@PathVariable Long eventId) {
        return ResponseEntity.ok(statsService.getEventStats(eventId));
    }

    /**
     * 부스 통계 (SELLER용)
     * GET /api/stats/booth/{partId}
     * → 일별 매출 추이, 상품별 판매량 순위
     */
    @GetMapping("/booth/{partId}")
    public ResponseEntity<BoothStatsResponse> getBoothStats(@PathVariable Long partId) {
        return ResponseEntity.ok(statsService.getBoothStats(partId));
    }
}
