package com.pospick.pospick.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponse {
    private Long prodId;
    private String name;
    private int price;
    private int stockQuantity;
    private boolean isSoldOut;
    private String imageUrl;
    private String aiCategory;
}
