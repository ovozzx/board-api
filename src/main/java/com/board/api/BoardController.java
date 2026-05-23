package com.board.api;

import com.board.converter.BoardConverter;
import com.board.dto.*;
import com.board.service.BoardService;
import com.board.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

// TODO 암호 key 512 권장
// TODO 스웨거 성공/**실패** 응답 표준화 필요 (프론트 입장 고려), 먼저 정하고 시작
// 200으로 받고, 바디에 에러 코드 넣어서 에러 핸들링하는 경우 존재 -> 맞지 않음 (응답 바디에 상태 코드 있으면 안됨)
// http://localhost:8081/swagger-ui/index.html
@Tag(name = "게시판", description = "게시글 CRUD API")
@RestController
@RequestMapping("/api/boards")
@CrossOrigin(origins = "*")
public class BoardController {

    @Autowired
    private BoardConverter boardConverter;

    @Autowired
    private BoardService service;

    @Operation(summary = "게시글 목록 조회", description = "검색 조건(날짜, 카테고리, 키워드)과 페이지 번호로 게시글 목록을 조회합니다.") // API 제목과 설명
    @ApiResponses({ // 응답 코드별 설명 (200, 400, 403 등)
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<BoardsResponse> getBoards(@ModelAttribute BoardsRequest boardsRequest) {
        // DTO → VO 변환은 컨트롤러가 담당 (서비스가 BoardsRequest를 모르게)
        SearchVO searchVO = boardConverter.from(boardsRequest);

        // 시나리오를 컨트롤러에 노출 — 화면 개발자가 어떤 데이터들이 모이는지 한눈에 보임
        List<CategoryVO> categoryList = service.getCategories();
        List<BoardVO> boardList = service.getBoardList(searchVO);
        int totalCount = service.getBoardListCount(searchVO);

        // 응답 DTO 조립도 컨트롤러가 담당 (서비스가 응답 형태를 모르게)
        BoardsResponse response = new BoardsResponse(categoryList, boardList, totalCount, searchVO);

        return ResponseEntity.ok(response); // 200
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 게시글 상세 정보, 댓글, 첨부파일을 조회합니다. 조회 시 조회수가 증가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailResponse> getBoard(
            @Parameter(description = "게시글 ID", required = true) @PathVariable int boardId){

        BoardVO board = service.getBoardById(boardId);
        List<AttachmentVO> fileList = service.getAttachments(boardId);
        List<ReplyVO> replyList = service.getReplyList(boardId);
        service.updateViewCount(boardId);

        return ResponseEntity.ok(new BoardDetailResponse(board, replyList, fileList));
    }

    @Operation(summary = "게시글 수정을 위한 조회", description = "게시글 ID로 게시글 상세 정보, 댓글, 첨부파일을 조회합니다. 조회 시 조회수가 증가하지 않습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{boardId}/modify")
    public ResponseEntity<BoardDetailResponse> getBoardForModify(
            @Parameter(description = "게시글 ID", required = true) @PathVariable int boardId){
        BoardVO board = service.getBoardById(boardId);
        List<AttachmentVO> fileList = service.getAttachments(boardId);

        return ResponseEntity.ok(new BoardDetailResponse(board, null, fileList));
    }

    @Operation(summary = "게시글 작성을 위한 카테고리 목록 조회", description = "게시글 작성 시 사용할 카테고리 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/cateogories")
    public ResponseEntity<List<CategoryVO>> getCategories(){
        List<CategoryVO> categories = service.getCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "첨부파일 다운로드", description = "파일 ID로 첨부파일을 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "404", description = "파일 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
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
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
                .body(resource); // 실제 파일 데이터를 응답 바디에 실어서 전송
    }

    @Operation(summary = "게시글 등록", description = "게시글을 등록합니다. 첨부파일은 최대 3개까지 업로드 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "필수값 누락"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
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
        service.registerBoard(boardConverter.from(requestBoardWrite)); // 나누지 말기 (정적 보안 소스 툴에 걸림)
        return ResponseEntity.ok().build();


    }

    @Operation(summary = "게시글 수정", description = "비밀번호 검증 후 게시글을 수정합니다. 첨부파일 삭제 및 신규 업로드가 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "필수값 누락"),
            @ApiResponse(responseCode = "403", description = "비밀번호 불일치"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{boardId}")
    public ResponseEntity<?> modifyBoard(@Valid @ModelAttribute BoardModifyRequest requestBoardModify) throws IOException {
        BoardVO boardVO = boardConverter.from(requestBoardModify);
        service.modifyBoard(boardVO);
        return ResponseEntity.ok().build();

    }

    @Operation(summary = "게시글 삭제", description = "비밀번호 검증 후 게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "필수값 누락"),
            @ApiResponse(responseCode = "403", description = "비밀번호 불일치"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable int boardId, @Valid @RequestBody BoardDeleteRequest requestBoardDelete) throws IOException {
        // 이 흐름이 맞음
        // 컨트롤러 말고 global 핸들러에서 하도록 (한곳에서 처리)
        // 첨부파일, 댓글이 있는 게시물 삭제! => 실제 바이너리 삭제는 어떻게 할지 (정책에 따라)
        // 삭제 시, 본문 글만 지우는 경우 등 여러 케이스 존재 (고민).. cascade
        // TODO : 조건 분기
        // 게시물 없음 (400), 비밀번호 불일치 (403), 삭제 성공 (200), 삭제 실패 (400)

        service.deleteBoard(boardId, requestBoardDelete.getPasswordInput()); // 결과 cnt 피하기
        return ResponseEntity.ok().build();

    }

    @Operation(summary = "댓글 등록", description = "게시글에 댓글을 등록합니다. 4 depth 답글 등록이 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "필수값 누락"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{boardId}/replies")
    public ResponseEntity<?> registerReply(@Valid @RequestBody ReplyWriteRequest replyWriteRequest) { // form 방식일 때만 vo 자동 바인딩, json은 @RequestBody 필요
        ReplyVO replyVO = boardConverter.from(replyWriteRequest);
        service.registerReply(replyVO);
        return ResponseEntity.ok().build(); // build() : body 없음
    }



}
