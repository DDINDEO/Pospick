package com.pospick.pospick.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Gemini AI 카테고리 분류 결과 DTO
 * - mainCategory: 메인 카테고리 (예: "문구/취미")
 * - subCategory: 서브 카테고리 (예: "캐릭터 굿즈")
 * - confidence: 분류 신뢰도 (0.0 ~ 1.0, 높을수록 확실)
 */
@Getter
@AllArgsConstructor
public class CategoryClassifyResponse {
    private final String mainCategory;
    private final String subCategory;
    private final double confidence;
}
