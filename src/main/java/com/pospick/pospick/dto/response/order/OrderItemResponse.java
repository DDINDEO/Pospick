package com.pospick.pospick.dto.response.order;

import com.pospick.pospick.domain.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemResponse {

    private final Long itemId;
    private final Long prodId;
    private final String productName;
    private final int quantity;
    private final int priceAtOrder;     // 주문 당시 가격 (나중에 가격 변경돼도 보존)
    private final int subtotal;         // 소계 = priceAtOrder * quantity

    public OrderItemResponse(OrderItem item) {
        this.itemId = item.getItemId();
        this.prodId = item.getProduct().getProdId();
        this.productName = item.getProduct().getName();
        this.quantity = item.getQuantity();
        this.priceAtOrder = item.getPriceAtOrder();
        this.subtotal = item.getPriceAtOrder() * item.getQuantity();
    }
}
