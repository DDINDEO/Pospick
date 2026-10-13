package com.pospick.pospick.dto.response.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EventStatsResponse {
    private final int totalRevenue;
    private final int totalOrders;
    private final List<BoothStatItem> boothStats;
    private final List<CategoryStatItem> categoryStats;
    private final List<DailyStatItem> dailyStats;           // 일별 매출 추이
    private final List<PaymentStatItem> paymentMethodStats; // 결제 수단 비율
    private final List<HourlyStatItem> hourlyStats;         // 시간대별 피크

    @Getter @AllArgsConstructor
    public static class BoothStatItem {
        private final Long partId;
        private final String sellerName;
        private final int revenue;
        private final int orderCount;
    }

    @Getter @AllArgsConstructor
    public static class CategoryStatItem {
        private final String category;
        private final int quantity;
        private final int revenue;
    }

    @Getter @AllArgsConstructor
    public static class DailyStatItem {
        private final String date;
        private final int revenue;
        private final int orderCount;
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
}
