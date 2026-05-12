package com.pospick.pospick.service;

import com.pospick.pospick.domain.Participation;
import com.pospick.pospick.domain.Product;
import com.pospick.pospick.dto.request.product.ProductCreateRequest;
import com.pospick.pospick.dto.response.product.CategoryClassifyResponse;
import com.pospick.pospick.dto.response.product.ProductResponse;
import com.pospick.pospick.exception.CustomException;
import com.pospick.pospick.repository.ParticipationRepository;
import com.pospick.pospick.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 상품 비즈니스 로직을 처리하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ParticipationRepository participationRepository;
    private final S3Service s3Service;
    private final GeminiService geminiService; // AI 카테고리 분류 서비스

    /**
     * 상품 등록
     * - SELLER가 자신의 부스(Participation)에 상품 등록
     *
     * @param request 상품 정보 (partId, name, price, stockQuantity, imageUrl)
     */
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {

        CategoryClassifyResponse category = geminiService.classify(request.name()); //gemini 분류

        // 참가 신청 조회 (부스 확인)
        Participation participation = participationRepository.findById(request.partId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 참가 신청입니다."));

        // 승인된 참가 신청에만 상품 등록 가능
        if (!participation.getStatus().equals("APPROVED")) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "승인된 참가 신청에만 상품을 등록할 수 있습니다.");
        }

        // 상품 생성
        Product product = new Product();
        product.setParticipation(participation);
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setImageUrl(request.imageUrl());
        product.setAiCategory(category.getMainCategory() + " > " + category.getSubCategory()); //gemini 카테고리 분류 생성

        return new ProductResponse(productRepository.save(product));
    }

    /**
     * 부스별 상품 목록 조회
     * - 특정 참가 신청(부스)에 등록된 상품 전체 조회
     *
     * @param partId 참가 신청 ID
     */
    public List<ProductResponse> getProducts(Long partId) {
        Participation participation = participationRepository.findById(partId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 참가 신청입니다."));

        return productRepository.findByParticipation(participation).stream()
                .map(ProductResponse::new)
                .toList();
    }

    /**
     * 상품 수정
     * - 상품명, 가격, 재고 수정 가능
     *
     * @param prodId 수정할 상품 ID
     * @param request 수정할 상품 정보
     */

    @Transactional
    public ProductResponse updateProduct(Long prodId, ProductCreateRequest request) {
        Product product = productRepository.findById(prodId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."));

        product.setName(request.name());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setImageUrl(request.imageUrl());

        return new ProductResponse(product);
    }

    /**
     * 상품 삭제
     * - 상품 삭제 시 S3에 저장된 이미지도 함께 삭제
     *
     * @param prodId 삭제할 상품 ID
     */
    @Transactional
    public void deleteProduct(Long prodId) {
        Product product = productRepository.findById(prodId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."));

        // S3에 이미지가 있으면 함께 삭제
        s3Service.delete(product.getImageUrl());

        productRepository.delete(product);
    }

    /**
     * 상품 이미지 업로드
     * - 이미지를 S3에 업로드하고 상품의 imageUrl 업데이트
     *
     * @param prodId 이미지를 등록할 상품 ID
     * @param file   업로드할 이미지 파일
     * @return 업데이트된 상품 정보
     */
    @Transactional
    public ProductResponse uploadImage(Long prodId, MultipartFile file) {
        Product product = productRepository.findById(prodId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."));

        // 기존 이미지가 있으면 S3에서 먼저 삭제
        s3Service.delete(product.getImageUrl());

        // 새 이미지 S3 업로드 후 URL 저장
        String imageUrl = s3Service.upload(file);
        product.setImageUrl(imageUrl);

        return new ProductResponse(product);
    }
}
