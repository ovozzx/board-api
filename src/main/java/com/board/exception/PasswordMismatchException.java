package com.board.exception;

// 403 - 비밀번호 불일치 등
public class PasswordMismatchException  extends RuntimeException{
    public PasswordMismatchException(String message) { super(message); }
}
