package com.board.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class RequestBoardModify {
    private String boardId;
    private String categoryId;
    private String categoryName;
    private String title;
    private String content;
    private String createUser;
    private int viewCount;
    private String createDate;
    private String modifyDate;
    private String useYn;
    private String passwordInput;
    private List<String> deleteIds;
    private List<MultipartFile> attachmentList;

}
