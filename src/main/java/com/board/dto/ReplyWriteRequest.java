package com.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ReplyWriteRequest {

	@Schema(hidden = true)
	private int replyId;
    @Schema(description = "게시글 ID", example = "1")
    private int boardId;
    @Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "0")
    private Integer parentReplyId;
    @Schema(description = "작성자", example = "홍길동")
    private String createUser;
    @Schema(description = "댓글 내용", example = "좋은 글이네요!")
    private String content;
    @Schema(hidden = true)
    private String createDate;
    @Schema(hidden = true)
    private String modifyDate;
    @Schema(hidden = true)
    private String useYn;
    
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
