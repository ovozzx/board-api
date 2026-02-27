package com.board.vo;

import static com.board.constants.BoardConstants.PAGE_SIZE;

public class SearchVO {
	private String startDate;
	private String endDate;
	private int categoryId;
	private String keyword;
	private int page;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return PAGE_SIZE; // XML에서 #{pageSize}로 참조
	}

	public int getOffset() {
		// 맵퍼에서 연산식 못 넣어서, 계산하고 넘겨야 함
		// int p = page < 1 ? 1 : page;
		// http://localhost:8081/board/list?startDate=2025-02-13&endDate=2026-02-13&categoryId=0&keyword=ee
		if(page == 0) page = 1;
		return (page - 1) * PAGE_SIZE;
	}
}
