package com.pospick.pospick.dto.response.product;

import com.pospick.pospick.domain.Product;
import lombok.Getter;

/**
 * 상품 응답 DTO
 * - prodId: 상품 ID
 * - name: 상품명
 * - price: 가격
 * - stockQuantity: 재고 수량
 * - isSoldOut: 품절 여부
 * - imageUrl: 상품 이미지 URL
 * - aiCategory: AI 분류 카테고리 (추후 사용)
 */
@Getter
public class ProductResponse {

    private final Long prodId;
    private final String name;
    private final int price;
    private final int stockQuantity;
    private final boolean isSoldOut;
    private final String imageUrl;
    private final String aiCategory;

    /**
     * Product 엔티티로부터 응답 DTO 생성
     */
    public ProductResponse(Product product) {
        this.prodId = product.getProdId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.stockQuantity = product.getStockQuantity();
        this.isSoldOut = product.isSoldOut();
        this.imageUrl = product.getImageUrl();
        this.aiCategory = product.getAiCategory();
    }
}
