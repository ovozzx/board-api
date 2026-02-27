package com.board.dto;

import lombok.Data;

import static com.board.constants.BoardConstants.PAGE_SIZE;

@Data
public class BoardsRequest {
	private String startDate;
	private String endDate;
	private Integer categoryId;
	private String keyword;
	private Integer page;

	public int getPageSize() {
		return PAGE_SIZE;
	}

	public int getOffset() {
		int p = (page == null || page < 1) ? 1 : page;
		return (p - 1) * PAGE_SIZE;
	}
}
