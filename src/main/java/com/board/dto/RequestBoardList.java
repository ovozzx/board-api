package com.board.dto;

import lombok.Data;

import static com.board.constants.BoardConstants.PAGE_SIZE;

@Data
public class RequestBoardList {
	private String startDate;
	private String endDate;
	private String categoryId;
	private String keyword;
	private int page;

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
