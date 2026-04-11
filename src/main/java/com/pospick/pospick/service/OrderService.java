package com.pospick.pospick.service;

import com.pospick.pospick.repository.OrderItemRepository;
import com.pospick.pospick.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // TODO: 주문 생성 - 상품 선택 → 합계 계산 → Order + OrderItem 저장 → 재고 차감
    // TODO: 거스름돈 계산 - receivedAmount - totalPrice
    // TODO: 주문 목록 조회 - 참가(partId) 기준 조회
    // TODO: 주문 단건 조회
    // TODO: 주문 취소 - 재고 복구
}
