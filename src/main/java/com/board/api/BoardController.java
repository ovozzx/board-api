package com.board.api;

import com.board.dto.*;
import com.board.service.BoardService;
import com.board.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



// http://localhost:8081/swagger-ui/index.html
@Tag(name = "게시판", description = "게시글 CRUD API")
@RestController
@RequestMapping("/api/boards")
@CrossOrigin(origins = "*")
public class BoardController {


    @Autowired
    private BoardService service;

    @Operation(summary = "게시글 목록 조회", description = "검색 조건(날짜, 카테고리, 키워드)과 페이지 번호로 게시글 목록을 조회합니다.") // API 제목과 설명
    @ApiResponses({ // 응답 코드별 설명 (200, 400, 403 등)
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<BoardsResponse> getBoards(@ModelAttribute BoardsRequest boardsRequest) {
        // list -> 복수, /id, http method
        LocalDate today = LocalDate.now();
        // dto 한테 맡기기 => validate 기능 사용 (request dto 모두)
        if (boardsRequest.getStartDate() == null || boardsRequest.getStartDate().isEmpty()) {
            boardsRequest.setStartDate(today.minusYears(1).toString());
        }

        if (boardsRequest.getEndDate() == null || boardsRequest.getEndDate().isEmpty()) {
            boardsRequest.setEndDate(today.toString());
        }

        // 의존성 끊기
        SearchVO searchVO = SearchVO.from(boardsRequest); // 컨트롤러에서 이루어져야 함. 알고만 있어도 의존성이 생김 끊어야 함

        BoardsResponse response = service.getBoards(searchVO); // TODO: 서비스에서 dto를 알고있음 -> 맵퍼도 컨트롤러에 있는 게 맞음
        response.setStartDate(boardsRequest.getStartDate());
        response.setEndDate(boardsRequest.getEndDate()); // TODO 매퍼쓰도록 --> 많을 때를 고려해서 수동으로 안하도록 => getBoards 안에서 하는 게 맞을듯 (dto/vo 분리가 안되고 있음)

        return ResponseEntity.ok(response); // 200
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 게시글 상세 정보, 댓글, 첨부파일을 조회합니다. 조회 시 조회수가 증가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/{boardId}") // /board/id.. view 수정
    public ResponseEntity<BoardDetailResponse> getBoard(
            @Parameter(description = "게시글 ID", required = true) @PathVariable int boardId){ // 파라미터 설명
        BoardDetailResponse boardDetailViewVO = service.getDetailBoardById(boardId);
        List<ReplyVO> replyList = service.getReplyList(boardId);
        boardDetailViewVO.setReplyList(replyList);
        service.updateViewCount(boardId);

        return ResponseEntity.ok(boardDetailViewVO);
        //  null => 서비스에서 예외 던지기 => 컨트롤러 던지기 => 공통 예외처리

    }

    @Operation(summary = "게시글 수정을 위한 조회", description = "게시글 ID로 게시글 상세 정보, 댓글, 첨부파일을 조회합니다. 조회 시 조회수가 증가하지 않습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/{boardId}/modify") //
    public ResponseEntity<BoardDetailResponse> getBoardForModify(
            @Parameter(description = "게시글 ID", required = true) @PathVariable int boardId){ // 파라미터 설명
        BoardDetailResponse boardDetailViewVO = service.getDetailBoardById(boardId);

        return ResponseEntity.ok(boardDetailViewVO);

    }

    @Operation(summary = "게시글 작성을 위한 카테고리 목록 조회", description = "게시글 작성 시 사용할 카테고리 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/cateogories")
    public ResponseEntity<List<CategoryVO>> getCategories(){
        List<CategoryVO> categories = service.getCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "첨부파일 다운로드", description = "파일 ID로 첨부파일을 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "404", description = "파일 없음")
    })
    @GetMapping("/{boardId}/attachments/{attachmentId}")
    public ResponseEntity<Resource> getAttachment(
            @Parameter(description = "파일 ID", required = true) @PathVariable int attachmentId) throws IOException {
        AttachmentVO attachment = service.getAttachmentById(attachmentId);

        if (attachment == null) {
            return ResponseEntity.notFound().build();
        }

        File file = new File(attachment.getFilePath() + File.separator + attachment.getSaveName());

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file); // 디스크의 파일을 HTTP 응답 바디로 스트리밍할 수 있게 래핑
        String encodedName = URLEncoder.encode(attachment.getOriginalName(), "UTF-8")
                                       .replaceAll("\\+", "%20");
        // TODO : 통으로 읽어서 내려받기 or 부분 부분만 내려받기
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
                .body(resource); // 실제 파일 데이터를 응답 바디에 실어서 전송
    }

    @Operation(summary = "게시글 등록", description = "게시글을 등록합니다. 첨부파일은 최대 3개까지 업로드 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "403", description = "필수값 누락 또는 등록 실패")
    })
    @PostMapping
    public ResponseEntity<?> writeBoard(@Valid @ModelAttribute BoardWriteRequest requestBoardWrite) throws IOException {

        // valid dto에 넣기
        // @Valid만 붙이면 DTO의 @NotBlank 등이 자동 실행되고, 결과가 BindingResult에 담김
        // bindingResult.getAllErrors() -> 에러 객체 안에 그 message 값이 담김
        // 아래 유효성 검사 유틸화
        // DTO 어노테이션 & 파라미터 @Valid --> BindingResult가 없으면, validation 실패 시 Spring이 자동으로 MethodArgumentNotValidException 던짐!
//        if (bindingResult.hasErrors()) {
//            Map<String, String> errors = new HashMap<>();
//            bindingResult.getFieldErrors().forEach(error -> // validation에 걸린 필드만 들어있음
//                    errors.put(error.getField(), error.getDefaultMessage())
//            );
//            return ResponseEntity.badRequest().body(errors);
//        }

        service.registerBoard(requestBoardWrite); // void, 성공시 등록 결과 return / 실패면 예외 던지기 ===> 프론트 : 받은 데이터를 활용 (등록 패턴 통일) CRD
        return ResponseEntity.ok().build();


    }

    @Operation(summary = "게시글 수정", description = "비밀번호 검증 후 게시글을 수정합니다. 첨부파일 삭제 및 신규 업로드가 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "필수값 누락"),
            @ApiResponse(responseCode = "403", description = "비밀번호 불일치")
    })
    @PutMapping("/{boardId}")
    public ResponseEntity<?> modifyBoard(@Valid @ModelAttribute BoardModifyRequest requestBoardModify) throws IOException {
        // board, 첨부파일, 삭제
        System.out.println("삭제 id : " + requestBoardModify.getDeleteIds());

        service.modifyBoard(requestBoardModify);
        return ResponseEntity.ok().build();

    }

    @Operation(summary = "게시글 삭제", description = "비밀번호 검증 후 게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "비밀번호 불일치"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> deleteBoard(@RequestBody BoardDeleteRequest requestBoardDelete) throws IOException {
        // 이 흐름이 맞음
        // 컨트롤러 말고 global 핸들러에서 하도록 (한곳에서 처리)
        // 첨부파일, 댓글이 있는 게시물 삭제! => 실제 바이너리 삭제는 어떻게 할지 (정책에 따라)
        // 삭제 시, 본문 글만 지우는 경우 등 여러 케이스 존재 (고민).. cascade
        // TODO : 조건 분기
        // 게시물 없음 (400), 비밀번호 불일치 (403), 삭제 성공 (200), 삭제 실패 (400)

        service.deleteBoard(requestBoardDelete); // 결과 cnt 피하기
        return ResponseEntity.ok().build();

    }

    @Operation(summary = "댓글 등록", description = "게시글에 댓글을 등록합니다. 4 depth 답글 등록이 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "403", description = "필수값 누락"),
    })
    @PostMapping("/{boardId}/replies")
    public ResponseEntity<?> registerReply(@Valid @RequestBody ReplyWriteRequest replyWriteRequest) { // form 방식일 때만 vo 자동 바인딩, json은 @RequestBody 필요

        service.registerReply(replyWriteRequest);
        return ResponseEntity.ok().build(); // build() : body 없음
    }



}
