package com.pospick.pospick.dto.request.product;

import lombok.Getter;

@Getter
public class ProductCreateRequest {
    private Long partId;
    private String name;
    private int price;
    private int stockQuantity;
    private String imageUrl;
}
