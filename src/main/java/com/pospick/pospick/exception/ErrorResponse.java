package com.pospick.pospick.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 에러 응답 형식
 * 에러 발생 시 클라이언트에게 반환되는 JSON 구조
 * 예: { "status": 404, "message": "존재하지 않는 행사입니다." }
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;     // HTTP 상태 코드
    private String message; // 에러 메시지
}
