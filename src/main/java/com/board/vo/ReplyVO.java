package com.board.vo;

import lombok.Data;

@Data
public class ReplyVO {

	private int replyId;        // REPLY_ID
    private int boardId;        // BOARD_ID
    private Integer parentReplyId;  // PARENT_REPLY_ID (null 가능)
    private String createUser;   // CREATE_USER
    private String content;      // CONTENT
    private String createDate; // CREATE_DATE
    private String modifyDate; // MODIFY_DATE
    private String useYn;        // USE_YN
	private int depth;
	private String path;

}
