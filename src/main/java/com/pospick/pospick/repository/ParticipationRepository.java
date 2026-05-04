package com.pospick.pospick.repository;

import com.pospick.pospick.domain.Event;
import com.pospick.pospick.domain.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 참가 신청 데이터 접근 레이어
 */
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    /**
     * 특정 행사의 참가 신청 목록 조회
     */
    List<Participation> findByEvent(Event event);

    /**
     * 특정 행사 + 특정 판매자의 참가 신청 존재 여부 확인 (중복 신청 방지)
     */
    boolean existsByEventAndSeller_UserId(Event event, Long sellerId);
}
