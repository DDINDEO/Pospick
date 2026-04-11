package com.pospick.pospick.dto.request.order;

import lombok.Getter;
import java.util.List;

@Getter
public class OrderCreateRequest {
    private Long partId;
    private String paymentMethod; // CASH, TRANSFER, COMPLEX
    private int receivedAmount;
    private List<OrderItemRequest> items;

    @Getter
    public static class OrderItemRequest {
        private Long prodId;
        private int quantity;
    }
}
