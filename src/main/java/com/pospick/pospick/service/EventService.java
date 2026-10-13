package com.pospick.pospick.service;

import com.pospick.pospick.domain.Event;
import com.pospick.pospick.domain.User;
import com.pospick.pospick.dto.request.event.EventCreateRequest;
import com.pospick.pospick.dto.response.event.EventResponse;
import com.pospick.pospick.exception.CustomException;
import com.pospick.pospick.repository.*;
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
    private final ParticipationRepository participationRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    /**
     * 행사 생성
     * @param request 행사 정보 (제목, 날짜 등) — organizerId 불필요
     * @param loginId JWT 토큰에서 추출한 로그인 ID → 이걸로 DB에서 사용자 조회
     * → 로그인한 ORGANIZER가 자동으로 주최자로 등록됨
     */
    @Transactional
    public EventResponse createEvent(EventCreateRequest request, String loginId) {
        // JWT 토큰의 loginId로 사용자 조회 → 요청 바디에 organizerId 없어도 됨
        User organizer = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

        Event event = new Event();
        event.setOrganizer(organizer);
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setLocation(request.location());
        event.setStartDate(request.startDate());
        event.setEndDate(request.endDate());
        event.setPosterUrl(request.posterUrl());

        // 수집 옵션 저장 - ORGANIZER가 설정한 매출/재고 수집 여부
        event.setCollectSales(request.collectSales());
        event.setCollectStock(request.collectStock());

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

    /**
     * 행사 삭제
     * - 연결된 참가 신청(Participation) → 상품(Product) → 주문(Order/OrderItem) 순으로 먼저 삭제
     * @param eventId 삭제할 행사 ID
     */
    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 행사입니다."));

        // 이 행사에 연결된 참가 신청 목록 조회
        List<com.pospick.pospick.domain.Participation> participations =
                participationRepository.findByEvent(event);

        for (com.pospick.pospick.domain.Participation part : participations) {
            // 부스에 연결된 주문 → 주문 아이템 삭제
            List<com.pospick.pospick.domain.Order> orders =
                    orderRepository.findByParticipation(part);
            for (com.pospick.pospick.domain.Order order : orders) {
                orderItemRepository.deleteAll(orderItemRepository.findByOrder(order));
            }
            orderRepository.deleteAll(orders);

            // 부스에 연결된 상품 삭제
            productRepository.deleteAll(productRepository.findByParticipation(part));
        }

        // 참가 신청 삭제
        participationRepository.deleteAll(participations);

        // 행사 삭제
        eventRepository.delete(event);
    }

    /**
     * 내가 만든 행사 목록 조회 (ORGANIZER 마이페이지)
     * - JWT 토큰의 loginId로 내 행사 목록 반환
     *
     * @param loginId JWT 토큰에서 추출한 로그인 ID
     */
    public List<EventResponse> getMyEvents(String loginId) {
        // loginId로 주최자 조회
        User organizer = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));

        // 내가 만든 행사 목록 반환
        return eventRepository.findByOrganizer(organizer).stream()
                .map(EventResponse::new)
                .toList();
    }
}
