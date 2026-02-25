package com.board.dto;

import com.board.vo.AttachmentVO;
import com.board.vo.BoardVO;
import lombok.Data;

import java.util.List;

@Data
public class ResponseBoardModify {
    private BoardVO board;
    private List<AttachmentVO> fileList;
}
