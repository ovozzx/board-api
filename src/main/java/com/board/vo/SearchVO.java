package com.board.vo;

import com.board.dto.BoardsRequest;
import lombok.Data;

@Data
public class SearchVO {

	public static SearchVO from(BoardsRequest request) { //  BoardsRequest → SearchVO로 타입 변환 + null 처리 로직
		SearchVO vo = new SearchVO();
		vo.startDate = request.getStartDate();
		vo.endDate = request.getEndDate();
		vo.categoryId = request.getCategoryId() != null ? request.getCategoryId() : 0;
		vo.keyword = request.getKeyword();
		vo.page = request.getPage() != null ? request.getPage() : 1;
		vo.pageSize = request.getPageSize() != 0 ? request.getPageSize() : 10;
		return vo;
	}

	private String startDate;
	private String endDate;
	private int categoryId;
	private String keyword;
	private int page;
	private int pageSize;
	private int offset;

	private int getOffset(){
		return (page - 1) * pageSize;
	}
}
