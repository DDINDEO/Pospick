package com.pospick.pospick.controller;

import com.pospick.pospick.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/participations")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    // TODO: POST  /api/participations              - 참가 신청 (Seller)
    // TODO: PATCH /api/participations/{id}/status  - 승인/거절 (Organizer)
    // TODO: GET   /api/participations/event/{id}   - 행사별 신청 목록 (Organizer)
    // TODO: GET   /api/participations/my           - 내 참가 목록 (Seller)
}
