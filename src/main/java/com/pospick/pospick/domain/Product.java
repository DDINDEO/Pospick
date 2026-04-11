package com.pospick.pospick.domain;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "products")
@Getter @Setter @NoArgsConstructor
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Participation participation;

    private String name;
    private int price;
    private int stockQuantity = 0;
    private String imageUrl;
    private String aiCategory;
    private boolean isSoldOut = false;
}