package com.board.vo;

import com.board.dto.BoardsRequest;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchVO {
	// TODO 매퍼에서 아래 작업하는 게 맞을듯 (**일관성**) -> 클로드 쓸 때도 md로
//	public static SearchVO from(BoardsRequest request) { //  BoardsRequest → SearchVO로 타입 변환 + null 처리 로직
//		SearchVO vo = new SearchVO();
//		LocalDate today = LocalDate.now();
//		String startDateFallback = today.minusYears(1).toString();
//		String endDateFallback = today.toString();
//		vo.startDate = (request.getStartDate() == null || request.getStartDate().isEmpty()) ?
//				        startDateFallback : request.getStartDate();
//		vo.endDate = (request.getEndDate() == null || request.getEndDate().isEmpty()) ?
//		             endDateFallback : request.getEndDate();
//		vo.categoryId = request.getCategoryId() != null ? request.getCategoryId() : 0;
//		vo.keyword = request.getKeyword();
//		vo.page = request.getPage() != null ? request.getPage() : 1;
//		vo.pageSize = request.getPageSize() != 0 ? request.getPageSize() : 10;
//		return vo;
//	}

	private String startDate;
	private String endDate;
	private int categoryId;
	private String keyword;
	private int page;
	private int pageSize;
	private int offset;

	private int getOffset(){
		return (page - 1) * pageSize;
	} // mybatis에서 사용
}
