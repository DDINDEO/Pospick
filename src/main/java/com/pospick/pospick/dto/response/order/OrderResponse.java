package com.pospick.pospick.dto.response.order;

import com.pospick.pospick.domain.Order;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderResponse {

    private final Long orderId;
    private final Long partId;
    private final int totalPrice;
    private final String paymentMethod;
    private final int receivedAmount;
    private final int changeAmount;       // 거스름돈 = receivedAmount - totalPrice
    private final LocalDateTime createdAt;
    private final List<OrderItemResponse> items;

    public OrderResponse(Order order, List<OrderItemResponse> items) {
        this.orderId = order.getOrderId();
        this.partId = order.getParticipation().getPartId();
        this.totalPrice = order.getTotalPrice();
        this.paymentMethod = order.getPaymentMethod();
        this.receivedAmount = order.getReceivedAmount();
        this.changeAmount = order.getReceivedAmount() - order.getTotalPrice(); // 거스름돈 자동 계산
        this.createdAt = order.getCreatedAt();
        this.items = items;
    }
}
