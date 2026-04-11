package com.pospick.pospick.dto.request.event;

import lombok.Getter;
import java.time.LocalDate;

/**
 * 행사 생성 요청 DTO
 * 클라이언트가 POST /api/events 호출 시 보내는 데이터
 */
@Getter
public class EventCreateRequest {
    private Long organizerId; // TODO: 3주차 JWT 도입 후 토큰에서 자동 추출로 변경
    private String title;       // 행사 이름
    private String description; // 행사 설명
    private String location;    // 행사 장소
    private LocalDate startDate; // 행사 시작일
    private LocalDate endDate;   // 행사 종료일
    private String posterUrl;   // 행사 포스터 이미지 URL
}
