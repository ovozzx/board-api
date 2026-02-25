package com.board.vo;

import com.board.mapper.BoardMapper;
import jdk.jfr.Category;

import java.util.List;

public class BoardListViewVO {
    List<CategoryVO> categoryList;
    List<BoardVO> boardList;
    int boardListCount;

    public List<CategoryVO> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryVO> categoryList) {
        this.categoryList = categoryList;
    }

    public List<BoardVO> getBoardList() {
        return boardList;
    }

    public void setBoardList(List<BoardVO> boardList) {
        this.boardList = boardList;
    }

    public int getBoardListCount() {
        return boardListCount;
    }

    public void setBoardListCount(int boardListCount) {
        this.boardListCount = boardListCount;
    }

}
