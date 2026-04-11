package com.pospick.pospick.controller;

import com.pospick.pospick.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // TODO: POST /api/orders       - 주문 생성 (Seller)
    // TODO: GET  /api/orders       - 주문 목록 조회
    // TODO: GET  /api/orders/{id}  - 주문 단건 조회
    // TODO: DELETE /api/orders/{id} - 주문 취소 (재고 복구)
}
