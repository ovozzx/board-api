package com.board.dto;

import com.board.vo.AttachmentVO;
import com.board.vo.BoardVO;
import com.board.vo.ReplyVO;
import lombok.Data;

import java.util.List;

@Data
public class BoardDetailResponse {
    private BoardVO board;
    private List<ReplyVO> replyList;
    private List<AttachmentVO> fileList;

    public BoardDetailResponse(BoardVO board, List<ReplyVO> replyList, List<AttachmentVO> fileList){
        this.board = board;
        this.replyList = replyList;
        this.fileList = fileList;
    }

}
