package com.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class BoardDeleteRequest {
    @Schema(description = "비밀번호", example = "aa1234*")
    String passwordInput;
    @Schema(description = "게시글 ID", example = "1")
    int boardId;

    public String getPasswordInput() {
        return passwordInput;
    }

    public void setPasswordInput(String passwordInput) {
        this.passwordInput = passwordInput;
    }

    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

}
