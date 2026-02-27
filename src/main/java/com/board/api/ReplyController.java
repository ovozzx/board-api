package com.board.api;

import com.board.dto.ReplyWriteRequest;
import com.board.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reply")
@CrossOrigin(origins = "http://localhost:3000")
public class ReplyController {

    @Autowired
    private ReplyService service;

    @PostMapping("/write")
    public ResponseEntity<?> registerReply(@RequestBody ReplyWriteRequest replyWriteRequest) { // form 방식일 때만 vo 자동 바인딩, json은 @RequestBody 필요
        service.registerReply(replyWriteRequest);
        return ResponseEntity.ok().build(); // build() : body 없음
    }

}
