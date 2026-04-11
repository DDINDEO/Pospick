package com.pospick.pospick.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 프로젝트 전용 커스텀 예외 클래스
 * 사용 예: throw new CustomException(HttpStatus.NOT_FOUND, "존재하지 않는 행사입니다.");
 */
@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;  // HTTP 상태 코드 (404, 400, 403 등)
    private final String message;     // 클라이언트에게 보여줄 에러 메시지

    public CustomException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
