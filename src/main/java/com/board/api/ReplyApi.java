package com.board.api;

import com.board.dto.RequestReplyWrite;
import com.board.service.ReplyService;
import com.board.vo.ReplyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reply")
@CrossOrigin(origins = "http://localhost:3000")
public class ReplyApi {

    @Autowired
    private ReplyService service;

    /**
     * 댓글 등록하기
     * @param reply
     * @param model
     * @return view
     */
    @PostMapping("/write")
    public ResponseEntity<?> registerReply(@RequestBody RequestReplyWrite requestReplyWrite) { // form 방식일 때만 vo 자동 바인딩, json은 @RequestBody 필요
        boolean isRegistered = service.registerReply(requestReplyWrite);
        //System.out.println("=== 댓글 등록 : " + result);
        if(isRegistered){
            return ResponseEntity.ok().build(); // build() : body 없음
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

}
