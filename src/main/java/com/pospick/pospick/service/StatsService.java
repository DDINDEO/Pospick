package com.pospick.pospick.service;

import com.pospick.pospick.domain.*;
import com.pospick.pospick.dto.response.stats.BoothStatsResponse;
import com.pospick.pospick.dto.response.stats.BoothStatsResponse.DailyStatItem;
import com.pospick.pospick.dto.response.stats.BoothStatsResponse.ProductStatItem;
import com.pospick.pospick.dto.response.stats.BoothStatsResponse.StockStatItem;
import com.pospick.pospick.dto.response.stats.EventStatsResponse;
import com.pospick.pospick.dto.response.stats.EventStatsResponse.BoothStatItem;
import com.pospick.pospick.exception.CustomException;
import com.pospick.pospick.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final EventRepository eventRepository;
    private final ParticipationRepository participationRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    // ─────────────────────────────────────────────
    // ORGANIZER용 행사 통계
    // ─────────────────────────────────────────────
    public EventStatsResponse getEventStats(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 행사입니다."));

        List<Participation> participations = participationRepository.findByEvent(event);

        int totalRevenue = 0;
        int totalOrders = 0;
        List<BoothStatItem> boothStats = new ArrayList<>();

        Map<String, int[]> categoryMap = new LinkedHashMap<>();
        Map<String, int[]> dailyMap = new TreeMap<>();       // 일별 매출
        Map<String, int[]> paymentMap = new LinkedHashMap<>(); // 결제 수단별
        Map<Integer, int[]> hourlyMap = new TreeMap<>();     // 시간대별

        for (Participation part : participations) {
            List<Order> orders = orderRepository.findByParticipation(part);
            int boothRevenue = orders.stream().mapToInt(Order::getTotalPrice).sum();

            totalRevenue += boothRevenue;
            totalOrders += orders.size();

            boothStats.add(new BoothStatItem(
                    part.getPartId(),
                    part.getSeller().getName(),
                    boothRevenue,
                    orders.size()
            ));

            for (Order order : orders) {
                // 일별 집계
                String date = order.getCreatedAt().toLocalDate().toString();
                dailyMap.computeIfAbsent(date, k -> new int[]{0, 0});
                dailyMap.get(date)[0] += order.getTotalPrice();
                dailyMap.get(date)[1] += 1;

                // 결제 수단별 집계
                String method = order.getPaymentMethod() != null ? order.getPaymentMethod() : "기타";
                paymentMap.computeIfAbsent(method, k -> new int[]{0, 0});
                paymentMap.get(method)[0] += 1;
                paymentMap.get(method)[1] += order.getTotalPrice();

                // 시간대별 집계
                int hour = order.getCreatedAt().getHour();
                hourlyMap.computeIfAbsent(hour, k -> new int[]{0, 0});
                hourlyMap.get(hour)[0] += 1;
                hourlyMap.get(hour)[1] += order.getTotalPrice();

                // 카테고리별 집계
                List<OrderItem> items = orderItemRepository.findByOrder(order);
                for (OrderItem item : items) {
                    String category = item.getProduct().getAiCategory();
                    if (category == null || category.isBlank()) category = "기타";
                    String mainCat = category.contains(">") ? category.split(">")[0].trim() : category;

                    categoryMap.computeIfAbsent(mainCat, k -> new int[]{0, 0});
                    categoryMap.get(mainCat)[0] += item.getQuantity();
                    categoryMap.get(mainCat)[1] += item.getPriceAtOrder() * item.getQuantity();
                }
            }
        }

        boothStats.sort(Comparator.comparingInt(BoothStatItem::getRevenue).reversed());

        List<EventStatsResponse.CategoryStatItem> categoryStats = categoryMap.entrySet().stream()
                .map(e -> new EventStatsResponse.CategoryStatItem(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .sorted(Comparator.comparingInt(EventStatsResponse.CategoryStatItem::getRevenue).reversed())
                .toList();

        List<EventStatsResponse.DailyStatItem> dailyStats = dailyMap.entrySet().stream()
                .map(e -> new EventStatsResponse.DailyStatItem(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .toList();

        List<EventStatsResponse.PaymentStatItem> paymentMethodStats = paymentMap.entrySet().stream()
                .map(e -> new EventStatsResponse.PaymentStatItem(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .sorted(Comparator.comparingInt(EventStatsResponse.PaymentStatItem::getRevenue).reversed())
                .toList();

        List<EventStatsResponse.HourlyStatItem> hourlyStats = hourlyMap.entrySet().stream()
                .map(e -> new EventStatsResponse.HourlyStatItem(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .toList();

        return new EventStatsResponse(totalRevenue, totalOrders, boothStats, categoryStats,
                dailyStats, paymentMethodStats, hourlyStats);
    }

    // ─────────────────────────────────────────────
    // SELLER용 부스 통계
    // ─────────────────────────────────────────────
    public BoothStatsResponse getBoothStats(Long partId) {

        Participation participation = participationRepository.findById(partId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 부스입니다."));

        List<Order> orders = orderRepository.findByParticipation(participation);

        int totalRevenue = orders.stream().mapToInt(Order::getTotalPrice).sum();
        int totalOrders = orders.size();

        Map<String, int[]> dailyMap = new TreeMap<>();
        Map<String, int[]> productMap = new LinkedHashMap<>();
        Map<String, int[]> categoryMap = new LinkedHashMap<>();
        Map<String, int[]> paymentMap = new LinkedHashMap<>(); // 결제 수단별
        Map<Integer, int[]> hourlyMap = new TreeMap<>();       // 시간대별

        for (Order order : orders) {
            // 일별 집계
            String date = order.getCreatedAt().toLocalDate().toString();
            dailyMap.computeIfAbsent(date, k -> new int[]{0, 0});
            dailyMap.get(date)[0] += order.getTotalPrice();
            dailyMap.get(date)[1] += 1;

            // 결제 수단별 집계
            String method = order.getPaymentMethod() != null ? order.getPaymentMethod() : "기타";
            paymentMap.computeIfAbsent(method, k -> new int[]{0, 0});
            paymentMap.get(method)[0] += 1;
            paymentMap.get(method)[1] += order.getTotalPrice();

            // 시간대별 집계
            int hour = order.getCreatedAt().getHour();
            hourlyMap.computeIfAbsent(hour, k -> new int[]{0, 0});
            hourlyMap.get(hour)[0] += 1;
            hourlyMap.get(hour)[1] += order.getTotalPrice();

            List<OrderItem> items = orderItemRepository.findByOrder(order);
            for (OrderItem item : items) {
                // 상품별 집계
                String productName = item.getProduct().getName();
                productMap.computeIfAbsent(productName, k -> new int[]{0, 0});
                productMap.get(productName)[0] += item.getQuantity();
                productMap.get(productName)[1] += item.getPriceAtOrder() * item.getQuantity();

                // 카테고리별 집계
                String category = item.getProduct().getAiCategory();
                if (category == null || category.isBlank()) category = "기타";
                String mainCat = category.contains(">") ? category.split(">")[0].trim() : category;

                categoryMap.computeIfAbsent(mainCat, k -> new int[]{0, 0});
                categoryMap.get(mainCat)[0] += item.getQuantity();
                categoryMap.get(mainCat)[1] += item.getPriceAtOrder() * item.getQuantity();
            }
        }

        List<DailyStatItem> dailyStats = dailyMap.entrySet().stream()
                .map(e -> new DailyStatItem(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .toList();

        List<ProductStatItem> productStats = productMap.entrySet().stream()
                .map(e -> new ProductStatItem(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .sorted(Comparator.comparingInt(ProductStatItem::getQuantity).reversed())
                .limit(10)
                .toList();

        List<BoothStatsResponse.CategoryStatItem> categoryStats = categoryMap.entrySet().stream()
                .map(e -> new BoothStatsResponse.CategoryStatItem(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .sorted(Comparator.comparingInt(BoothStatsResponse.CategoryStatItem::getQuantity).reversed())
                .toList();

        List<BoothStatsResponse.PaymentStatItem> paymentMethodStats = paymentMap.entrySet().stream()
                .map(e -> new BoothStatsResponse.PaymentStatItem(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .sorted(Comparator.comparingInt(BoothStatsResponse.PaymentStatItem::getRevenue).reversed())
                .toList();

        List<BoothStatsResponse.HourlyStatItem> hourlyStats = hourlyMap.entrySet().stream()
                .map(e -> new BoothStatsResponse.HourlyStatItem(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .toList();

        // ── 재고 소진율 계산 ──────────────────────────
        // productMap에 이미 상품별 판매량이 집계되어 있음
        // Product의 stockQuantity는 현재 남은 재고 (판매 시 차감됨)
        List<Product> products = productRepository.findByParticipation(participation);
        List<StockStatItem> stockStats = products.stream()
                .map(product -> {
                    int sold = productMap.containsKey(product.getName())
                            ? productMap.get(product.getName())[0]
                            : 0;
                    int remaining = product.getStockQuantity();
                    int initial = remaining + sold;
                    double rate = initial > 0 ? Math.round((double) sold / initial * 1000) / 10.0 : 0.0;
                    return new StockStatItem(product.getName(), initial, sold, remaining, rate);
                })
                .sorted(Comparator.comparingDouble(StockStatItem::getDepletionRate).reversed())
                .toList();

        return new BoothStatsResponse(totalRevenue, totalOrders, dailyStats, productStats,
                categoryStats, paymentMethodStats, hourlyStats, stockStats);
    }
}
