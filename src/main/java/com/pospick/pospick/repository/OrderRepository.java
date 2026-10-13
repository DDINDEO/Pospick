package com.pospick.pospick.repository;

import com.pospick.pospick.domain.Order;
import com.pospick.pospick.domain.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 특정 부스의 주문 목록 조회 (매출 내역 확인용)
     */
    List<Order> findByParticipation(Participation participation);
}
