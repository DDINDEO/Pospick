package com.pospick.pospick.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity @Table(name = "events")
@Getter @Setter @NoArgsConstructor
public class Event {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User organizer; // 주최자

    private String title;
    private String posterUrl;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt = LocalDateTime.now();

    // 수집 옵션 - ORGANIZER가 행사 생성 시 설정
    // true면 해당 데이터를 수집하겠다는 의미
    private boolean collectSales = false;  // 매출 데이터 수집 여부
    private boolean collectStock = false;  // 재고 데이터 수집 여부
}