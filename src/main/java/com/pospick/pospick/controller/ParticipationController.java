package com.pospick.pospick.controller;

import com.pospick.pospick.dto.request.participation.ParticipationRequest;
import com.pospick.pospick.dto.response.participation.ParticipationResponse;
import com.pospick.pospick.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 행사 참가 신청 API 컨트롤러
 */
@RestController
@RequestMapping("/api/participations")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    /**
     * 참가 신청 (SELLER 전용)
     * POST /api/participations
     * 요청 바디: { eventId }
     * @AuthenticationPrincipal → JWT 필터에서 저장한 loginId 자동 주입
     */
    @PostMapping
    public ResponseEntity<ParticipationResponse> apply(
            @AuthenticationPrincipal String loginId,
            @RequestBody ParticipationRequest request) {
        return ResponseEntity.ok(participationService.apply(loginId, request));
    }

    /**
     * 참가 신청 승인 (ORGANIZER 전용)
     * PATCH /api/participations/{id}/approve
     */
    @PatchMapping("/{id}/approve")
    public ResponseEntity<ParticipationResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(participationService.approve(id));
    }

    /**
     * 참가 신청 거절 (ORGANIZER 전용)
     * PATCH /api/participations/{id}/reject
     */
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ParticipationResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(participationService.reject(id));
    }

    /**
     * 행사별 참가 신청 목록 조회 (ORGANIZER 전용)
     * GET /api/participations?eventId={id}
     */
    @GetMapping
    public ResponseEntity<List<ParticipationResponse>> getParticipations(
            @RequestParam Long eventId) {
        return ResponseEntity.ok(participationService.getParticipations(eventId));
    }
}
