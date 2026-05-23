package com.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BoardDeleteRequest {
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Schema(description = "비밀번호", example = "aa1234*")
    String passwordInput;
    @Schema(description = "게시글 ID", example = "1")
    int boardId;
}
