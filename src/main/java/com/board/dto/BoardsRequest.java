package com.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;



@Data
public class BoardsRequest {
	@Schema(description = "검색 시작일", example = "2025-01-01")
	private String startDate;
	@Schema(description = "검색 종료일", example = "2025-12-31")
	private String endDate;
	@Schema(description = "카테고리 ID", example = "0")
	private Integer categoryId;
	@Schema(description = "검색 키워드", example = "")
	private String keyword;
	@Schema(description = "페이지 번호", example = "1")
	private Integer page;
	@Schema(description = "페이지 단위로 표시할 게시물 수", example = "10")
	private int pageSize;

}
