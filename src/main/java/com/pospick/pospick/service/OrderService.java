package com.pospick.pospick.service;

import com.pospick.pospick.domain.*;
import com.pospick.pospick.dto.request.order.OrderCreateRequest;
import com.pospick.pospick.dto.request.order.OrderItemRequest;
import com.pospick.pospick.dto.response.order.OrderItemResponse;
import com.pospick.pospick.dto.response.order.OrderResponse;
import com.pospick.pospick.exception.CustomException;
import com.pospick.pospick.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 주문(결제) 비즈니스 로직 서비스
 * - 주문 생성: 재고 차감 + Order/OrderItem DB 저장 + 거스름돈 계산
 * - 주문 조회: 부스별 매출 내역
 * - 주문 취소: 재고 복구
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ParticipationRepository participationRepository;
    private final ProductRepository productRepository;

    /**
     * 주문 생성 (결제)
     * 1. 부스 확인
     * 2. 각 상품 재고 확인 → 재고 차감
     * 3. Order 저장 (합계, 결제수단, 받은금액)
     * 4. OrderItem 저장 (상품별 수량, 주문 당시 가격)
     * 5. 거스름돈 = receivedAmount - totalPrice
     */
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {

        // 1. 부스 확인
        Participation participation = participationRepository.findById(request.partId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 부스입니다."));

        // 2. 각 상품 재고 확인 및 차감
        int totalPrice = 0;
        for (OrderItemRequest itemReq : request.items()) {
            Product product = productRepository.findById(itemReq.prodId())
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다. ID: " + itemReq.prodId()));

            // 재고 부족 체크
            if (product.getStockQuantity() < itemReq.quantity()) {
                throw new CustomException(HttpStatus.BAD_REQUEST,
                        product.getName() + " 재고가 부족합니다. (현재 재고: " + product.getStockQuantity() + ")");
            }

            // 재고 차감
            product.setStockQuantity(product.getStockQuantity() - itemReq.quantity());

            // 합계 누적
            totalPrice += product.getPrice() * itemReq.quantity();
        }

        // 3. 거스름돈 검증 (현금 결제 시 받은 금액이 총액보다 적으면 에러)
        if ("CASH".equals(request.paymentMethod()) && request.receivedAmount() < totalPrice) {
            throw new CustomException(HttpStatus.BAD_REQUEST,
                    "받은 금액이 부족합니다. (합계: " + totalPrice + "원, 받은 금액: " + request.receivedAmount() + "원)");
        }

        // 4. Order 저장
        Order order = new Order();
        order.setParticipation(participation);
        order.setTotalPrice(totalPrice);
        order.setPaymentMethod(request.paymentMethod());
        order.setReceivedAmount(request.receivedAmount());
        Order savedOrder = orderRepository.save(order);

        // 5. OrderItem 저장 (상품별)
        List<OrderItem> orderItems = request.items().stream().map(itemReq -> {
            Product product = productRepository.findById(itemReq.prodId()).get(); // 위에서 검증 완료
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(product);
            item.setQuantity(itemReq.quantity());
            item.setPriceAtOrder(product.getPrice()); // 주문 당시 가격 저장 (나중에 가격 바뀌어도 보존)
            return item;
        }).toList();

        orderItemRepository.saveAll(orderItems);

        // 6. 응답 반환 (거스름돈 포함)
        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(OrderItemResponse::new)
                .toList();

        return new OrderResponse(savedOrder, itemResponses);
    }

    /**
     * 부스별 주문 목록 조회 (매출 내역)
     *
     * @param partId 부스 ID
     */
    public List<OrderResponse> getOrders(Long partId) {
        Participation participation = participationRepository.findById(partId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 부스입니다."));

        return orderRepository.findByParticipation(participation).stream()
                .map(order -> {
                    List<OrderItemResponse> items = orderItemRepository.findByOrder(order).stream()
                            .map(OrderItemResponse::new)
                            .toList();
                    return new OrderResponse(order, items);
                })
                .toList();
    }

    /**
     * 주문 단건 조회
     *
     * @param orderId 주문 ID
     */
    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다."));

        List<OrderItemResponse> items = orderItemRepository.findByOrder(order).stream()
                .map(OrderItemResponse::new)
                .toList();

        return new OrderResponse(order, items);
    }

    /**
     * 주문 취소 (재고 복구)
     * - 취소 시 차감했던 재고를 원복
     *
     * @param orderId 취소할 주문 ID
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다."));

        // 각 OrderItem의 수량만큼 재고 복구
        List<OrderItem> items = orderItemRepository.findByOrder(order);
        for (OrderItem item : items) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity()); // 재고 복구
        }

        orderRepository.delete(order);
    }
}
