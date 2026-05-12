package com.pospick.pospick.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.pospick.pospick.dto.response.product.CategoryClassifyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Gemini AI 카테고리 분류 서비스
 * - 상품명을 Gemini에게 보내면 메인/서브 카테고리 + 신뢰도 자동 분류
 * - 예: "키링" → { main: "문구/취미", sub: "캐릭터 굿즈", confidence: 0.95 }
 */
@Slf4j
@Service
public class GeminiService {

    private final Client client;

    public GeminiService(@Value("${gemini.api-key}") String apiKey) {
        // API 키로 Gemini 클라이언트 생성
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    /**
     * 상품명으로 카테고리 자동 분류
     *
     * @param productName 분류할 상품명
     * @return CategoryClassifyResponse (mainCategory, subCategory, confidence)
     */
    public CategoryClassifyResponse classify(String productName) {
        try {
            // Gemini에게 보낼 프롬프트
            // JSON 형식으로만 응답하도록 강제, 괄호 안 예시는 참고용
            String prompt = """
                    아래 카테고리 목록을 참고해서 상품명을 분류해줘.
                    반드시 JSON 형식으로만 답해줘. 다른 말은 하지 마.
                    sub_category는 괄호 안 예시 제외하고 앞의 이름만 써줘.

                    카테고리 목록 (괄호 안은 예시):
                    - 식음료 (F&B): 간편식/스트리트푸드 (떡볶이, 꼬치, 타코야끼), 베이커리/디저트 (구움과자, 마카롱, 케이크), 커피/차 (원두, 드립백, 티백, 콜드브루), 음료/주스 (에이드, 수제청, 탄산), 주류 (수제맥주, 와인, 전통주, 하이볼), 가공식품 (잼, 장아찌, 밀키트, 소스)
                    - 패션/잡화: 의류 (티셔츠, 빈티지, 후드, 에코백), 주얼리/액세서리 (귀걸이, 반지, 목걸이, 팔찌), 잡화 (모자, 양말, 장갑, 손수건), 신발/가방 (스니커즈, 가죽 가방, 파우치)
                    - 문구/팬시 (Stationery): 스티커/테이프 (씰스티커, 마스킹테이프, 롤스티커), 지류/기록 (노트, 다이어리, 메모지, 플래너), 엽서/포스터 (그림엽서, 아트포스터, 사진), 필기구 (볼펜, 만년필, 샤프, 마커), 데코/굿즈 (키링, 뱃지, 마그넷, 스마트톡), 수납/바인더 (필통, 바인더, 스티커북, 속지), 디지털 액세서리 (폰케이스, 워치스트랩, 패드파우치)
                    - 리빙/취미/도서: 향기/욕실 (캔들, 디퓨저, 수제비누, 배스밤), 인테리어 소품 (조명, 거울, 화분, 액자), 주방용품 (도자기, 유리컵, 식기류, 컵코스터), 도서/출판 (독립출판물, 잡지, 그림책, 진), DIY/예술용품 (취미키트, 물감, 붓, 컬러링북), 반려동물 용품 (간식, 의류, 장난감)
                    - 뷰티/헬스: 기초/색조 (화장품, 핸드크림, 립밤), 바디/헤어 (샴푸바, 바디로션, 오일), 건강 (영양제, 단백질 쉐이크, 요가용품)
                    - 서비스/경험: 워크숍/체험 (원데이클래스, 제작체험), 예술/상담 (캐리커처, 초상화, 타로, 사주), 이벤트/전시 (뽑기, 럭키박스, 티켓, 입장권)
                    - 기타: 중고 물품, 골동품, 분류 불가능(기타)

                    응답 형식 (JSON만):
                    {"main_category": "메인카테고리", "sub_category": "서브카테고리", "confidence": 신뢰도(0.0~1.0)}

                    상품명: %s
                    """.formatted(productName);

            // Gemini API 호출
            // gemini-2.5-flash: 현재 API 키에서 사용 가능한 최신 안정 버전, 무료 티어 지원
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    null
            );

            // 응답 텍스트에서 JSON 파싱
            String text = response.text().trim();
            log.info("Gemini 원본 응답: {}", text); // 디버그용

            // 혹시 마크다운 코드블록(```json ... ```)으로 감싸져 있으면 제거
            if (text.startsWith("```")) {
                text = text.replaceAll("```json", "").replaceAll("```", "").trim();
            }

            log.info("Gemini 분류 결과: {} → {}", productName, text);

            // JSON 수동 파싱 (Jackson 없이 간단하게)
            String mainCategory = extractJsonValue(text, "main_category");
            String subCategory = extractJsonValue(text, "sub_category");
            double confidence = Double.parseDouble(extractJsonValue(text, "confidence"));

            return new CategoryClassifyResponse(mainCategory, subCategory, confidence);

        } catch (Exception e) {
            // AI 분류 실패해도 상품 등록은 계속 진행
            log.error("Gemini 분류 실패: {} - {}", productName, e.getMessage(), e);
            return new CategoryClassifyResponse("기타", "분류 불가능", 0.0);
        }
    }

    /**
     * JSON 문자열에서 특정 키의 값 추출
     * 예: {"main_category": "문구/취미"} → "문구/취미"
     */
    private String extractJsonValue(String json, String key) {
        // "key": "value" 또는 "key": 0.95 형태 파싱
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return "기타";

        int colonIndex = json.indexOf(":", keyIndex);
        int valueStart = colonIndex + 1;

        // 앞 공백 제거
        while (valueStart < json.length() && json.charAt(valueStart) == ' ') valueStart++;

        // 문자열 값인지 숫자 값인지 판단
        if (json.charAt(valueStart) == '"') {
            // 문자열: "값"
            int valueEnd = json.indexOf("\"", valueStart + 1);
            return json.substring(valueStart + 1, valueEnd);
        } else {
            // 숫자: 0.95
            int valueEnd = valueStart;
            while (valueEnd < json.length() && (Character.isDigit(json.charAt(valueEnd)) || json.charAt(valueEnd) == '.')) {
                valueEnd++;
            }
            return json.substring(valueStart, valueEnd);
        }
    }
}
