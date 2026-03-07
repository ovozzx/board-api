package com.board.exception;

// 400 - 클라이언트 잘못된 요청
public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) { super(message); }
}
