package com.board.common;

import com.board.dto.ErrorResponse;
import com.board.exception.BadRequestException;
import com.board.exception.NotFoundException;
import com.board.exception.PasswordMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

// 대용량 다운로드 시 테스트
//  모든 Controller에서 발생하는 예외를 전역적으로 처리하는 클래스
// @ControllerAdvice // view 반환
// 서비스 메서드에서 checked 예외를 throws로 선언해서 컨트롤러까지 올려보내야 GlobalExceptionHandler에 도달
//
@RestControllerAdvice // json 반환
public class GlobalExceptionHandler { // Service에서 던진 예외는 GlobalExceptionHandler가 공통으로 처리
    // TODO : 맵퍼 쿼리 오류, db 연결, IO 등 그래도 던져지는 게 맞는지 (서비스 랩퍼)
    @ExceptionHandler(Exception.class) // 모든 예외의 최상위 부모 -> 구체적인 핸들러에 안 걸리는 예외는 전부 handleException에서 잡힘!
    public ResponseEntity<ErrorResponse> handleException(Exception e){ // 쿼리 오류 화면에 보이면 안됨
        return ResponseEntity.status(500).body(new ErrorResponse(500, e.getMessage())); // 에러 메세지 문자열로 하고 실제 내용은 log.error(e)에 기록 (보안)
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException e){
        return ResponseEntity.status(400).body(new ErrorResponse(400, e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(404).body(new ErrorResponse(404, e.getMessage()));
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatch(PasswordMismatchException e){
        return ResponseEntity.status(403).body(new ErrorResponse(403, e.getMessage())); //  401/403 (인증/권한)
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class) // 이 예외가 발생하면, 아래 메서드를 실행!
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(400).body(new ErrorResponse(400, "파일 용량이 초과되었습니다. (최대 10MB)")); // 잘못된 요청임
    }

    // 구체적인 핸들러를 우선 매칭


//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e){
//        return ResponseEntity.badRequest().body(e.getMessage());
//    }

    // TODO : 이외의 에러, io --> 에러 터지면 500
    // TODO : exception 체이닝, 런타임 / unchecked 조사
    // TODO : 커밋 이력 목적별로 남기기 (작업 단위) --> 답글 형태로 변환 (최대 4계층), 정렬 순서(등록일, 조회...)/페이지네이션 개수 -> 검색바 선택,
    // TODO : *** promise 코드 ***
    // status 코드 10개 내 외우기
    // http : 서버 - 브라우저 어떻게 통신
    // tcp, 소켓 .. cs 기초,,,,
}
