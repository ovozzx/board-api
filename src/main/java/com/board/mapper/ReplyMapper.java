package com.board.mapper;

import com.board.dto.RequestReplyWrite;
import com.board.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReplyMapper {
    int insertReply(RequestReplyWrite requestReplyWrite);
}
