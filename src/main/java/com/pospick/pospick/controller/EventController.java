package com.pospick.pospick.controller;

import com.pospick.pospick.dto.request.event.EventCreateRequest;
import com.pospick.pospick.dto.response.event.EventResponse;
import com.pospick.pospick.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 행사(Event) 관련 API 요청을 받는 컨트롤러
 * - 행사 생성, 목록 조회, 단건 조회
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * 행사 생성
     * POST /api/events
     * 요청 바디: EventCreateRequest (제목, 날짜, 장소 등)
     * @AuthenticationPrincipal → JWT 필터가 SecurityContext에 저장한 loginId를 자동으로 주입
     * → 요청 바디에 organizerId를 넣지 않아도 로그인한 사용자가 자동으로 주최자가 됨
     */
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @RequestBody EventCreateRequest request,
            @AuthenticationPrincipal String loginId  // JWT 토큰에서 추출한 로그인 ID
    ) {
        return ResponseEntity.ok(eventService.createEvent(request, loginId));
    }

    /**
     * 행사 전체 목록 조회
     * GET /api/events
     */
    @GetMapping
    public ResponseEntity<List<EventResponse>> getEvents() {
        return ResponseEntity.ok(eventService.getEvents());
    }

    /**
     * 행사 단건 조회
     * GET /api/events/{eventId}
     * @param eventId 조회할 행사 ID
     */
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEvent(eventId));
    }

    /**
     * 내가 만든 행사 목록 조회 (ORGANIZER 마이페이지)
     * GET /api/events/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<EventResponse>> getMyEvents(
            @AuthenticationPrincipal String loginId) {
        return ResponseEntity.ok(eventService.getMyEvents(loginId));
    }

    /**
     * 행사 삭제 (ORGANIZER 전용)
     * DELETE /api/events/{eventId}
     */
    @DeleteMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok("행사가 삭제됐습니다.");
    }
}
