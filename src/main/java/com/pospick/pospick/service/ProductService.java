package com.pospick.pospick.service;

import com.pospick.pospick.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    // TODO: 상품 등록 - 이름, 가격, 재고, 이미지 URL 저장
    // TODO: 상품 목록 조회 - 참가(partId) 기준으로 상품 리스트 반환
    // TODO: 상품 수정 - 이름, 가격, 재고 수정
    // TODO: 상품 삭제
    // TODO: 품절 처리 - isSoldOut = true
    // TODO: AI 카테고리 분류 연동 - 7주차 Gemini API 연동 후 채울 예정
}
