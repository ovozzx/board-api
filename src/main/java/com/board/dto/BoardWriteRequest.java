package com.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class BoardWriteRequest {

    @Schema(description = "게시글 ID", hidden = true)
    private int boardId;
    @Schema(description = "카테고리 ID", example = "1")
    private int categoryId;
    @Schema(description = "카테고리명", example = "자유게시판")
    private String categoryName;
    @NotBlank(message = "제목을 입력해 주세요.") // null, "", " " 모두 불가 (String 전용)
    @Schema(description = "제목", example = "테스트 게시글")
    private String title;
    @NotBlank(message = "내용을 입력해 주세요.")
    @Schema(description = "내용", example = "테스트 내용입니다.")
    private String content;
    @NotBlank(message = "작성자를 입력해 주세요.")
    @Schema(description = "작성자", example = "홍길동")
    private String createUser;
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Schema(description = "비밀번호", example = "aa1234*")
    private String userPassword;
    @Schema(hidden = true)
    private int viewCount;
    @Schema(hidden = true)
    private String createDate;
    @Schema(hidden = true)
    private String modifyDate;
    @Schema(hidden = true)
    private String useYn;
    @Schema(description = "첨부파일 유무")
	private boolean hasAttachment;
    @Schema(description = "첨부파일 목록")
	private List<MultipartFile> attachmentList;

}
