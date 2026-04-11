package com.pospick.pospick.controller;

import com.pospick.pospick.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // TODO: POST  /api/products                - 상품 등록 (Seller)
    // TODO: GET   /api/products?partId={id}    - 참가별 상품 목록 조회
    // TODO: PUT   /api/products/{id}           - 상품 수정 (Seller)
    // TODO: DELETE /api/products/{id}          - 상품 삭제 (Seller)
    // TODO: PATCH /api/products/{id}/soldout   - 품절 처리 (Seller)
}
