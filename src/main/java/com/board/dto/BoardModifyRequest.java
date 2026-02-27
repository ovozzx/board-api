package com.board.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class BoardModifyRequest {
    private int boardId;
    private int categoryId;
    private String categoryName;
    @NotBlank(message = "제목을 입력해 주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해 주세요.")
    private String content;
    @NotBlank(message = "작성자를 입력해 주세요.")
    private String createUser;
    private int viewCount;
    private String createDate;
    private String modifyDate;
    private String useYn;
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String passwordInput;
    private List<Integer> deleteIds;
    private List<MultipartFile> attachmentList;

}
