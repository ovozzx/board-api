package com.board.dto;

import com.board.vo.BoardVO;
import com.board.vo.CategoryVO;
import com.board.vo.PageInfo;
import com.board.vo.SearchVO;
import lombok.Data;

import java.util.List;

@Data
public class BoardsResponse {
    private List<CategoryVO> categoryList;
    private List<BoardVO> boardList;
    private int boardListCount;
    private PageInfo pageInfo;
    private SearchVO searchCondition;
    private String startDate;
    private String endDate;

    public BoardsResponse(List<CategoryVO> categoryList, List<BoardVO> boardList,
                          int boardListCount, PageInfo pageInfo) {
        this.categoryList = categoryList;
        this.boardList = boardList;
        this.boardListCount = boardListCount;
        this.pageInfo = pageInfo;
    }
}
