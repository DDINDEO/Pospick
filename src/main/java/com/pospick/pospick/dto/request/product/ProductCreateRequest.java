package com.pospick.pospick.dto.request.product;

/**
 * 상품 등록 요청 DTO
 */
public record ProductCreateRequest(
        Long partId,
        String name,
        int price,
        int stockQuantity,
        String imageUrl
) {}
