package com.pospick.pospick.controller;

import com.pospick.pospick.dto.request.order.OrderCreateRequest;
import com.pospick.pospick.dto.response.order.OrderResponse;
import com.pospick.pospick.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 주문(결제) API 컨트롤러
 * - SELLER만 접근 가능 (SecurityConfig에서 설정됨)
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성 (결제)
     * POST /api/orders
     * 재고 차감 + DB 저장 + 거스름돈 계산까지 한 번에 처리
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderCreateRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    /**
     * 부스별 주문 목록 조회 (매출 내역)
     * GET /api/orders?partId={id}
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@RequestParam Long partId) {
        return ResponseEntity.ok(orderService.getOrders(partId));
    }

    /**
     * 주문 단건 조회
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    /**
     * 주문 취소 (재고 복구)
     * DELETE /api/orders/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok("주문이 취소되었습니다.");
    }
}
