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

    // TODO : 이외의 에러, io --> 에러 터지면 500
    // TODO : exception 체이닝, 런타임 / unchecked 조사
    // TODO : 커밋 이력 목적별로 남기기 (작업 단위) --> 답글 형태로 변환 (최대 4계층), 정렬 순서(등록일, 조회...)/페이지네이션 개수 -> 검색바 선택,
    // TODO : *** promise 코드 ***
    // status 코드 10개 내 외우기
    // http : 서버 - 브라우저 어떻게 통신
    // tcp, 소켓 .. cs 기초,,,,
}
