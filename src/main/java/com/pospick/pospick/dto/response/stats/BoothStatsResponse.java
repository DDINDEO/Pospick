package com.pospick.pospick.dto.response.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BoothStatsResponse {
    private final int totalRevenue;
    private final int totalOrders;
    private final List<DailyStatItem> dailyStats;
    private final List<ProductStatItem> productStats;
    private final List<CategoryStatItem> categoryStats;
    private final List<PaymentStatItem> paymentMethodStats;  // 결제 수단별 매출
    private final List<HourlyStatItem> hourlyStats;          // 시간대별 피크
    private final List<StockStatItem> stockStats;            // 재고 소진율

    @Getter @AllArgsConstructor
    public static class DailyStatItem {
        private final String date;
        private final int revenue;
        private final int orderCount;
    }

    @Getter @AllArgsConstructor
    public static class ProductStatItem {
        private final String productName;
        private final int quantity;
        private final int revenue;
    }

    @Getter @AllArgsConstructor
    public static class CategoryStatItem {
        private final String category;
        private final int quantity;
        private final int revenue;
    }

    @Getter @AllArgsConstructor
    public static class PaymentStatItem {
        private final String method;   // CASH / TRANSFER / COMPLEX
        private final int orderCount;
        private final int revenue;
    }

    @Getter @AllArgsConstructor
    public static class HourlyStatItem {
        private final int hour;        // 0~23
        private final int orderCount;
        private final int revenue;
    }

    @Getter @AllArgsConstructor
    public static class StockStatItem {
        private final String productName;
        private final int initialStock;   // 최초 재고 (현재 재고 + 판매량)
        private final int soldQuantity;   // 판매량
        private final int remainingStock; // 현재 남은 재고
        private final double depletionRate; // 소진율 (%)
    }
}
