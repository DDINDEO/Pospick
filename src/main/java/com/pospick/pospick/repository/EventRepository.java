package com.pospick.pospick.repository;

import com.pospick.pospick.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
