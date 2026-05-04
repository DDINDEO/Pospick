package com.pospick.pospick.repository;

import com.pospick.pospick.domain.Participation;
import com.pospick.pospick.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 상품 데이터 접근 레이어
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 특정 참가 신청에 속한 상품 목록 조회
     */
    List<Product> findByParticipation(Participation participation);
}
