package com.pospick.pospick.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity @Table(name = "orders")
@Getter @Setter @NoArgsConstructor
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Participation participation;

    private int totalPrice;
    private String paymentMethod; // CASH, TRANSFER, COMPLEX
    private int receivedAmount;
    private LocalDateTime createdAt = LocalDateTime.now();
}