package com.pospick.pospick.dto.request.order;

import java.util.List;

/**
 * 주문 생성 요청 DTO
 */
public record OrderCreateRequest(
        Long partId,
        String paymentMethod,  // CASH, TRANSFER, COMPLEX
        int receivedAmount,
        List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            Long prodId,
            int quantity
    ) {}
}
