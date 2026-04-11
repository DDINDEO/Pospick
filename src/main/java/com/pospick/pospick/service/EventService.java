package com.pospick.pospick.service;

import com.pospick.pospick.domain.Event;
import com.pospick.pospick.domain.User;
import com.pospick.pospick.dto.request.event.EventCreateRequest;
import com.pospick.pospick.dto.response.event.EventResponse;
import com.pospick.pospick.exception.CustomException;
import com.pospick.pospick.repository.EventRepository;
import com.pospick.pospick.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 행사(Event) 비즈니스 로직을 처리하는 서비스
 * - 행사 생성, 목록 조회, 단건 조회
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 (조회 성능 최적화)
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    /**
     * 행사 생성
     * - organizerId로 주최자를 찾아 행사에 연결
     * - TODO: 3주차 JWT 도입 후 organizerId를 토큰에서 자동 추출로 변경
     */
    @Transactional // 데이터 변경이 있어서 별도로 선언
    public EventResponse createEvent(EventCreateRequest request) {
        // 주최자 조회 (없으면 404 에러)
        User organizer = userRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

        // 행사 엔티티 생성 및 값 세팅
        Event event = new Event();
        event.setOrganizer(organizer);
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setPosterUrl(request.getPosterUrl());

        // DB 저장 후 응답 DTO로 변환하여 반환
        return new EventResponse(eventRepository.save(event));
    }

    /**
     * 행사 전체 목록 조회
     * - DB에 있는 모든 행사를 EventResponse 리스트로 반환
     */
    public List<EventResponse> getEvents() {
        return eventRepository.findAll().stream()
                .map(EventResponse::new)
                .toList();
    }

    /**
     * 행사 단건 조회
     * - eventId로 행사를 찾아 반환
     * - 없으면 404 에러
     */
    public EventResponse getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 행사입니다."));
        return new EventResponse(event);
    }
}
