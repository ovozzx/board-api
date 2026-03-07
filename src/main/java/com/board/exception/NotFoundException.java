package com.board.exception;
// 404 - 리소스 없음
public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) { super(message); }
}
