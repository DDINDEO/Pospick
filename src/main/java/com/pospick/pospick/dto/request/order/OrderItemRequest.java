package com.pospick.pospick.dto.request.order;

public record OrderItemRequest(
        Long prodId,
        int quantity
) {}
