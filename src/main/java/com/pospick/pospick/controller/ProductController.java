package com.pospick.pospick.controller;

import com.pospick.pospick.dto.request.product.ProductCreateRequest;
import com.pospick.pospick.dto.response.product.ProductResponse;
import com.pospick.pospick.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 상품 API 컨트롤러
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 등록 (SELLER 전용)
     * POST /api/products
     * 요청 바디: { partId, name, price, stockQuantity, imageUrl }
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    /**
     * 부스별 상품 목록 조회
     * GET /api/products?partId={id}
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(@RequestParam Long partId) {
        return ResponseEntity.ok(productService.getProducts(partId));
    }

    /**
     * 상품 수정 (SELLER 전용)
     * PUT /api/products/{id}
     * 요청 바디: { name, price, stockQuantity, imageUrl }
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    /**
     * 상품 삭제 (SELLER 전용)
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("상품이 삭제되었습니다.");
    }

    /**
     * 상품 이미지 업로드 (SELLER 전용)
     * POST /api/products/{id}/image
     * 요청: multipart/form-data — 파라미터명 "file"
     * 응답: 업데이트된 상품 정보 (imageUrl 포함)
     */
    @PostMapping("/{id}/image")
    public ResponseEntity<ProductResponse> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(productService.uploadImage(id, file));
    }
}
