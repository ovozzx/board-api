package com.board.mapper;

import com.board.dto.RequestBoardModify;
import com.board.dto.RequestBoardWrite;
import com.board.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    List<BoardVO> selectBoardList(SearchVO searchVO);
    List<CategoryVO> selectCategoryList();
    BoardVO selectBoard(String boardId);
    int insertBoard(RequestBoardWrite requestBoardWrite);
    int updateBoard(RequestBoardModify requestBoardModify);
    int deleteBoard(String boardId);
    int updateViewCount(String boardId);
    String selectPasswordById(String boardId);
    int selectBoardListCount(SearchVO searchVO);

    List<AttachmentVO> selectFileList(String boardId);

    List<ReplyVO> selectReplyList(String boardId);

    int insertAttachment(AttachmentVO attachment);

    AttachmentVO selectAttachmentById(String attachmentId);

    int updateAttachment(AttachmentVO attachment);

    int deleteAttachment(String attachmentId);
}
