package com.board.vo;

import java.util.List;

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

	public int getBoardId() {
		return boardId;
	}
	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public int getViewCount() {
		return viewCount;
	}
	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
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

	public boolean isHasAttachment() {
		return hasAttachment;
	}

	public void setHasAttachment(boolean hasAttachment) {
		this.hasAttachment = hasAttachment;
	}

	@Override
	public String toString() {
		return "BoardVO{" +
				"boardId=" + boardId +
				", categoryId=" + categoryId +
				", categoryName='" + categoryName + '\'' +
				", title='" + title + '\'' +
				", content='" + content + '\'' +
				", createUser='" + createUser + '\'' +
				", userPassword='" + userPassword + '\'' +
				", viewCount=" + viewCount +
				", createDate=" + createDate +
				", modifyDate=" + modifyDate +
				", useYn='" + useYn + '\'' +
				'}';
	}
}
