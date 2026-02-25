package com.board.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class RequestBoardWrite {

    private String boardId;
    private String categoryId;
    private String categoryName;
    private String title;
    private String content;
    private String createUser;
    private String userPassword;
    private int viewCount;
    private String createDate;
    private String modifyDate;
    private String useYn;
	private boolean hasAttachment;
	private List<MultipartFile> attachmentList;

}
