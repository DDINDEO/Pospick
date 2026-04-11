package com.pospick.pospick.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity @Table(name = "participations")
@Getter @Setter @NoArgsConstructor
public class Participation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User seller; // 판매자

    private String status; // PENDING, APPROVED, REJECTED
    private boolean collectSalesAgree = false;
    private boolean collectStockAgree = false;
    private LocalDateTime agreedAt;
}