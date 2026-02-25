package com.board.common;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 컨트롤러 실행 중 예외가 발생했을 때
 * 또는 컨트롤러 호출 과정에서 예외가 발생했을 때
 * = DispatcherServlet 안에서 예외를 처리하는 메커니즘
 */
// TODO : 대용량 다운로드 시 테스트
@ControllerAdvice // 모든 Controller에서 발생하는 예외를 전역적으로 처리하는 클래스
public class GlobalExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class) // 이 예외가 발생하면, 아래 메서드를 실행!
    public String handleMaxSizeException(MaxUploadSizeExceededException e, Model model) {

        model.addAttribute("alertMsg"," 파일 용량이 초과되었습니다. (최대 10MB)");
        model.addAttribute("redirectUrl", "/board/write");

        return "board/alert";
    }
}
