package com.board.vo;

import java.util.List;

public class BoardModifyVO {
    private BoardVO board;
    private List<AttachmentVO> fileList;

    public BoardVO getBoard() {
        return board;
    }

    public void setBoard(BoardVO board) {
        this.board = board;
    }

    public List<AttachmentVO> getFileList() {
        return fileList;
    }

    public void setFileList(List<AttachmentVO> fileList) {
        this.fileList = fileList;
    }
}
