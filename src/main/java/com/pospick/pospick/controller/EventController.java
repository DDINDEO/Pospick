package com.pospick.pospick.controller;

import com.pospick.pospick.dto.request.event.EventCreateRequest;
import com.pospick.pospick.dto.response.event.EventResponse;
import com.pospick.pospick.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
     */
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventCreateRequest request) {
        return ResponseEntity.ok(eventService.createEvent(request));
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
}
