package com.board.service;

import com.board.dto.ReplyWriteRequest;
import com.board.mapper.ReplyMapper;
import com.board.vo.ReplyVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReplyService {

	private final ReplyMapper replyMapper;

	public ReplyService(ReplyMapper replyMapper) {
		this.replyMapper = replyMapper;
	}

	@Transactional
    public void registerReply(ReplyWriteRequest replyWriteRequest) {
		int insertCnt = replyMapper.insertReply(replyWriteRequest);
		if(insertCnt == 0){
			throw new IllegalArgumentException("댓글 등록에 실패하였습니다.");
		}
    }
}
