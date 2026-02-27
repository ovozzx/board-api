package com.board.mapper;

import com.board.dto.BoardModifyRequest;
import com.board.dto.BoardWriteRequest;
import com.board.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    List<BoardVO> selectBoardList(SearchVO searchVO);
    List<CategoryVO> selectCategoryList();
    BoardVO selectBoard(int boardId);
    int insertBoard(BoardWriteRequest requestBoardWrite);
    int updateBoard(BoardModifyRequest requestBoardModify);
    int deleteBoard(int boardId);
    int updateViewCount(int boardId);
    String selectPasswordById(int boardId);
    int selectBoardListCount(SearchVO searchVO);

    List<AttachmentVO> selectFileList(int boardId);

    List<ReplyVO> selectReplyList(int boardId);

    int insertAttachment(AttachmentVO attachment);

    AttachmentVO selectAttachmentById(int attachmentId);

    int updateAttachment(AttachmentVO attachment);

    int deleteAttachment(int attachmentId);
}
