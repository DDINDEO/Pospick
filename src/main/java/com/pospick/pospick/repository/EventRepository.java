package com.pospick.pospick.repository;

import com.pospick.pospick.domain.Event;
import com.pospick.pospick.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * 특정 주최자(ORGANIZER)가 만든 행사 목록 조회 (ORGANIZER 마이페이지)
     */
    List<Event> findByOrganizer(User organizer);
}
