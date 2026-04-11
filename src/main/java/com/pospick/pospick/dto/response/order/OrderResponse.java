package com.pospick.pospick.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private int totalPrice;
    private int receivedAmount;
    private int change; // 거스름돈
    private String paymentMethod;
    private LocalDateTime createdAt;
}
