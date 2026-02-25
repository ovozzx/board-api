package com.board.service;

import com.board.dto.RequestReplyWrite;
import com.board.mapper.BoardMapper;
import com.board.mapper.ReplyMapper;
import com.board.vo.ReplyVO;
import org.springframework.stereotype.Service;

@Service
public class ReplyService {

	private final ReplyMapper replyMapper;

	public ReplyService(ReplyMapper replyMapper) {
		this.replyMapper = replyMapper;
	}

	/**
	 * 댓글 등록하기
	 * @param reply
	 * @return boolean
	 */
    public boolean registerReply(RequestReplyWrite requestReplyWrite) {
		return replyMapper.insertReply(requestReplyWrite) > 0;
    }
}
