package com.pospick.pospick.service;

import com.pospick.pospick.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationService {

    private final ParticipationRepository participationRepository;

    // TODO: 참가 신청 - Seller가 행사에 신청, Participation 생성 (PENDING 상태)
    // TODO: 신청 승인/거절 - Organizer가 상태값 변경 (APPROVED / REJECTED)
    // TODO: 신청 목록 조회 - Organizer가 자신의 행사 신청 목록 조회
    // TODO: 내 참가 목록 조회 - Seller가 자신의 참가 현황 조회
}
