package com.board.vo;

import java.util.List;

public class BoardDetailViewVO {
    private BoardVO board;
    private List<ReplyVO> replyList;
    private List<AttachmentVO> fileList;

    public BoardVO getBoard() {
        return board;
    }

    public void setBoard(BoardVO board) {
        this.board = board;
    }

    public List<ReplyVO> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<ReplyVO> replyList) {
        this.replyList = replyList;
    }

    public List<AttachmentVO> getFileList() {
        return fileList;
    }

    public void setFileList(List<AttachmentVO> fileList) {
        this.fileList = fileList;
    }
}
