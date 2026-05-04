package com.pospick.pospick.service;

import com.pospick.pospick.domain.Event;
import com.pospick.pospick.domain.Participation;
import com.pospick.pospick.domain.User;
import com.pospick.pospick.dto.request.participation.ParticipationRequest;
import com.pospick.pospick.dto.response.participation.ParticipationResponse;
import com.pospick.pospick.exception.CustomException;
import com.pospick.pospick.repository.EventRepository;
import com.pospick.pospick.repository.ParticipationRepository;
import com.pospick.pospick.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 행사 참가 신청 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    /**
     * 참가 신청
     * - SELLER가 행사에 참가 신청 (PENDING 상태로 생성)
     * - 같은 행사에 중복 신청 불가
     *
     * @param loginId JWT 토큰에서 추출한 현재 로그인 사용자 ID
     * @param request 신청할 행사 ID
     */
    @Transactional
    public ParticipationResponse apply(String loginId, ParticipationRequest request) {
        // 신청자(SELLER) 조회
        User seller = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

        // 행사 조회
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 행사입니다."));

        // 중복 신청 확인
        if (participationRepository.existsByEventAndSeller_UserId(event, seller.getUserId())) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 신청한 행사입니다.");
        }

        // 참가 신청 생성 (PENDING 상태)
        Participation participation = new Participation();
        participation.setEvent(event);
        participation.setSeller(seller);
        participation.setStatus("PENDING");

        return new ParticipationResponse(participationRepository.save(participation));
    }

    /**
     * 참가 신청 승인
     * - ORGANIZER가 특정 참가 신청을 APPROVED로 변경
     *
     * @param partId 승인할 참가 신청 ID
     */
    @Transactional
    public ParticipationResponse approve(Long partId) {
        Participation participation = participationRepository.findById(partId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 참가 신청입니다."));

        // 이미 처리된 신청인지 확인
        if (!participation.getStatus().equals("PENDING")) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 처리된 참가 신청입니다.");
        }

        participation.setStatus("APPROVED");
        return new ParticipationResponse(participation);
    }

    /**
     * 참가 신청 거절
     * - ORGANIZER가 특정 참가 신청을 REJECTED로 변경
     *
     * @param partId 거절할 참가 신청 ID
     */
    @Transactional
    public ParticipationResponse reject(Long partId) {
        Participation participation = participationRepository.findById(partId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 참가 신청입니다."));

        // 이미 처리된 신청인지 확인
        if (!participation.getStatus().equals("PENDING")) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 처리된 참가 신청입니다.");
        }

        participation.setStatus("REJECTED");
        return new ParticipationResponse(participation);
    }

    /**
     * 특정 행사의 참가 신청 목록 조회
     * - ORGANIZER가 자신의 행사에 들어온 신청 목록 확인
     *
     * @param eventId 조회할 행사 ID
     */
    public List<ParticipationResponse> getParticipations(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 행사입니다."));

        return participationRepository.findByEvent(event).stream()
                .map(ParticipationResponse::new)
                .toList();
    }
}
