package com.board.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class BoardListResponseVO {
    private List<CategoryVO> categoryList;
    private List<BoardVO> boardList;
    private PageInfo pageInfo;
    private SearchVO searchCondition;
}