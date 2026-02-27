package com.board.mapper;

import com.board.dto.ReplyWriteRequest;
import com.board.vo.*;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReplyMapper {
    int insertReply(ReplyWriteRequest replyWriteRequest);
}
