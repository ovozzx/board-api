package com.board.vo;

public class ReplyVO {

	private int replyId;        // REPLY_ID
    private int boardId;        // BOARD_ID
    private Integer parentReplyId;  // PARENT_REPLY_ID (null 가능)
    private String createUser;   // CREATE_USER
    private String content;      // CONTENT
    private String createDate; // CREATE_DATE
    private String modifyDate; // MODIFY_DATE
    private String useYn;        // USE_YN
    
	public int getReplyId() {
		return replyId;
	}
	public void setReplyId(int replyId) {
		this.replyId = replyId;
	}
	public int getBoardId() {
		return boardId;
	}
	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}
	public Integer getParentReplyId() {
		return parentReplyId;
	}
	public void setParentReplyId(Integer parentReplyId) {
		this.parentReplyId = parentReplyId;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}
	public String getUseYn() {
		return useYn;
	}
	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}

    
}
