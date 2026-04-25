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
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    /**
     * 행사 생성
     * TODO: 3주차 JWT 도입 후 organizerId를 토큰에서 자동 추출로 변경
     */
    @Transactional
    public EventResponse createEvent(EventCreateRequest request) {
        User organizer = userRepository.findById(request.organizerId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

        Event event = new Event();
        event.setOrganizer(organizer);
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setLocation(request.location());
        event.setStartDate(request.startDate());
        event.setEndDate(request.endDate());
        event.setPosterUrl(request.posterUrl());

        return new EventResponse(eventRepository.save(event));
    }

    /**
     * 행사 전체 목록 조회
     */
    public List<EventResponse> getEvents() {
        return eventRepository.findAll().stream()
                .map(EventResponse::new)
                .toList();
    }

    /**
     * 행사 단건 조회
     */
    public EventResponse getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 행사입니다."));
        return new EventResponse(event);
    }
}
