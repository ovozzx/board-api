package com.board.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class BoardVO {

    private int boardId;
    private int categoryId;
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
    // TODO 엔티티가 MultipartFile를 아는 게 맞는가?, 목적별로 null인 필드가 많아짐
	private List<MultipartFile> attachmentList;
	private List<Integer> deleteIds;

}
