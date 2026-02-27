package com.board.common;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

// 대용량 다운로드 시 테스트
//  모든 Controller에서 발생하는 예외를 전역적으로 처리하는 클래스
// @ControllerAdvice // view 반환
@RestControllerAdvice // json 반환
public class GlobalExceptionHandler { // Service에서 던진 예외는 GlobalExceptionHandler가 공통으로 처리
    @ExceptionHandler(MaxUploadSizeExceededException.class) // 이 예외가 발생하면, 아래 메서드를 실행!
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException e) {
        return ResponseEntity.badRequest().body("파일 용량이 초과되었습니다. (최대 10MB)");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
