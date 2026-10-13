package com.pospick.pospick.dto.request.order;

import java.util.List;

public record OrderCreateRequest(
        Long partId,
        String paymentMethod,
        int receivedAmount,
        List<OrderItemRequest> items
) {}
