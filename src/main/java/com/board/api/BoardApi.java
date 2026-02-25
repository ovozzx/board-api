package com.board.api;

import com.board.dto.*;
import com.board.service.BoardService;
import com.board.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;

import static com.board.constants.BoardConstants.PAGE_GROUP_SIZE;
import static com.board.constants.BoardConstants.PAGE_SIZE;

// TODO :
//  list : 페이지네이션 : 이전 & 다음
//  + 피드백

/**
 * map structure 사용 필수
 * swagger 문서 뽑기
 * react 컴포넌트 많이 만들지말기 일단 되게만 하기 => 나중에 분리 ** next로 **
 * dto => request / response (dto 폴더 안에) (ex) requestWrite (엔드포인트당 2개씩, 아닌 경우 나타나면 고민해보기)
 * entity 만들어보기. dto, 엔티티 어디까지 쓰이는지 : 컨트롤러에서 엔티티로 전달 (내부 외부 단절)
 * 컨트롤러 : dto, 엔티티 둘다 알고 > 서비스 : dto > rep : 엔티티
 * 전달 데이터가 바뀔 때 컨트롤러는 바뀌어야 함 (서비스, repo는 그대로)
 * 없어도 되는 건 없어야 됨 (소스) => 즉시 삭제 (나중에 보면 어렵)
 * mybatis mapper 이름 => - 케밥케이스 xml
 * css, js, 정적 html => 웹에 노출되는 건 다 케밥케이스
 * 레퍼런스 타입, 프리미티브 타입 => 공부해오기 설명 가능하도록(메모리 사용 영역)
 * 디버깅 요령 : 시나리오 (추정 가설) 여러개 => 검증 (재현이 가능해야함)
 *  - end to end : 영역별로 쪼개서 찾음 
 */
// http://localhost:8081/swagger-ui/index.html
    // 데이터 예시, write
@Tag(name = "게시판", description = "게시글 CRUD API")
@RestController
@RequestMapping("/api/board")
@CrossOrigin(origins = "http://localhost:3000")
public class BoardApi {

    @Autowired
    private BoardService service;

    /**
     * 게시글 목록 조회
     * @param requestBoardList 검색 조건, 페이지
     * @return ResponseBoardList
     */
    @Operation(summary = "게시글 목록 조회", description = "검색 조건(날짜, 카테고리, 키워드)과 페이지 번호로 게시글 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/list")
    public ResponseEntity<ResponseBoardList> getBoardList(@ModelAttribute RequestBoardList requestBoardList) {
        // DTO : board list request (도메인단우ㅣ) , 파일명 controller로
        // ms api 가이드 (자료 첫장)
        // list -> 복수, /id, http method
        LocalDate today = LocalDate.now();
        // dto 한테 맡기기 => validate 기능 사용 (request dto 모두)
        if (requestBoardList.getStartDate() == null || requestBoardList.getStartDate().isEmpty()) {
            requestBoardList.setStartDate(today.minusYears(1).toString());
        }

        if (requestBoardList.getEndDate() == null || requestBoardList.getEndDate().isEmpty()) {
            requestBoardList.setEndDate(today.toString());
        }

        ResponseBoardList response = service.getBoardListView(requestBoardList);
        response.setStartDate(requestBoardList.getStartDate());
        response.setEndDate(requestBoardList.getEndDate());

        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 보기
     * @param boardId 게시글 ID
     * @return ResponseBoardDetail
     */
    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 게시글 상세 정보, 댓글, 첨부파일을 조회합니다. 조회 시 조회수가 증가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/view")
    public ResponseEntity<ResponseBoardDetail> getBoardDetailById(
            @Parameter(description = "게시글 ID", required = true) @RequestParam(required = false) String boardId){ // TODO : path v이 적절 (정적인 것)
        // id : 숫자면 숫자로 받기 앞에서 오류 나야함
        ResponseBoardDetail boardDetailViewVO = service.getDetailBoardById(boardId);

        return ResponseEntity.ok(boardDetailViewVO);
        // TODO : null => 서비스에서 예외 던지기 => 컨트롤러 던지기 => 공통 예외처리
    }

    /**
     * 게시글 작성 화면 카테고리 조회
     * @return 카테고리 목록
     */
    @Operation(summary = "카테고리 목록 조회", description = "게시글 작성 시 사용할 카테고리 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/write")
    public ResponseEntity<List<CategoryVO>> viewWritePage(){
        List<CategoryVO> categoryList = service.getCategoryList();
        return ResponseEntity.ok(categoryList);
    }

    /**
     * 게시글 수정 화면 데이터 조회
     * @param boardId 게시글 ID
     * @return ResponseBoardModify
     */
    @Operation(summary = "게시글 수정 데이터 조회", description = "게시글 ID로 수정 화면에 필요한 게시글 정보와 첨부파일 목록을 조회합니다. 조회수는 증가하지 않습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/modify")
    public ResponseEntity<ResponseBoardModify> viewModifyPage(
            @Parameter(description = "게시글 ID", required = true) @RequestParam(required = false) String boardId){ // path v 수정
        System.out.println("===" + boardId);
        // 수정 시에는 조회수 미증가
        ResponseBoardModify boardModifyVO = service.getModifyBoardById(boardId);

        return ResponseEntity.ok(boardModifyVO);
    }

    /**
     * 첨부파일 다운로드
     * @param fileId 파일 ID
     * @return ResponseEntity
     * @throws IOException
     */
    @Operation(summary = "첨부파일 다운로드", description = "파일 ID로 첨부파일을 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다운로드 성공"),
            @ApiResponse(responseCode = "404", description = "파일 없음")
    })
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "파일 ID", required = true) @RequestParam String fileId) throws IOException {
        AttachmentVO attachment = service.getAttachmentById(fileId);

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

    /**
     * 게시글 등록
     * @param requestBoardWrite 작성자, 비밀번호, 제목, 내용, 첨부파일
     * @return ResponseEntity
     * @throws IOException
     */
    @Operation(summary = "게시글 등록", description = "게시글을 등록합니다. 첨부파일은 최대 3개까지 업로드 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "403", description = "필수값 누락 또는 등록 실패")
    })
    @PostMapping("/write")
    public ResponseEntity<?> registerBoard(@ModelAttribute RequestBoardWrite requestBoardWrite) throws IOException {

        // 유효성 : 작성자, 비밀번호, 제목, 내용

        // TODO : valid dto에 넣기
        // 가독
        boolean isInvalid =
                        isBlank(requestBoardWrite.getCreateUser()) ||
                        isBlank(requestBoardWrite.getUserPassword()) ||
                        isBlank(requestBoardWrite.getTitle()) ||
                        isBlank(requestBoardWrite.getContent());

        if (isInvalid) {
            return ResponseEntity.status(403).build();
        }

        boolean isRegistered = service.registerBoard(requestBoardWrite); // TODO : void, 성공시 등록 결과 return / 실패면 예외 던지기 ===> 프론트 : 받은 데이터를 활용 (등록 패턴 통일) CRD
        if(isRegistered){
            return ResponseEntity.ok().build();
        } else{
            return ResponseEntity.status(403).build(); // 등록 실패 안내
        }
    }

    /**
     * 게시글 수정
     * @param requestBoardModify 작성자, 비밀번호, 제목, 내용, 삭제 파일 ID 목록, 신규 첨부파일
     * @return ResponseEntity
     * @throws IOException
     */
    @Operation(summary = "게시글 수정", description = "비밀번호 검증 후 게시글을 수정합니다. 첨부파일 삭제 및 신규 업로드가 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "필수값 누락"),
            @ApiResponse(responseCode = "403", description = "비밀번호 불일치")
    })
    @PostMapping("/modify")
    public ResponseEntity<?> modifyBoard(@ModelAttribute RequestBoardModify requestBoardModify) throws IOException {
        // board, 첨부파일, 삭제
        System.out.println("삭제 id : " + requestBoardModify.getDeleteIds());
        boolean isInvalid =
                isBlank(requestBoardModify.getCreateUser()) ||
                        isBlank(requestBoardModify.getPasswordInput()) ||
                        isBlank(requestBoardModify.getTitle()) ||
                        isBlank(requestBoardModify.getContent());

        if (isInvalid) {
            return ResponseEntity.status(400).body("필수값이 누락되었습니다.");
        }

        try{
            service.modifyBoard(requestBoardModify);
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(403).body("비밀번호가 일치하지 않거나, 수정 중 오류가 발생하였습니다.");
        }

        return ResponseEntity.ok().build();
    }

    /**
     * 게시글 삭제
     * @param requestBoardDelete 게시글 ID, 비밀번호
     * @return ResponseEntity
     * @throws IOException
     */
    @Operation(summary = "게시글 삭제", description = "비밀번호 검증 후 게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "비밀번호 불일치"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/delete")
    public ResponseEntity<?> deleteBoard(@RequestBody RequestBoardDelete requestBoardDelete) throws IOException {
        System.out.println("===" + requestBoardDelete.getPasswordInput());
        // 이 흐름이 맞음
        // 컨트롤러 말고 global 핸들러에서 하도록 (한곳에서 처리)
        // 첨부파일, 댓글이 있는 게시물 삭제! => 실제 바이너리 삭제는 어떻게 할지 (정책에 따라)
        // 삭제 시, 본문 글만 지우는 경우 등 여러 케이스 존재 (고민).. cascade
        // TODO : 조건 분기
        // 게시물 없음 (400), 비밀번호 불일치 (403), 삭제 성공 (200), 삭제 실패 (400)
        try{
            boolean isDeleted = service.deleteBoard(requestBoardDelete); // 결과 cnt 피하기
            if(isDeleted){
                return ResponseEntity.ok().build();
            }
            else{
                return ResponseEntity.status(403).build(); // 권한 없음
            }
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(500).build(); // 서버 오류 : 삭제 중 오류
        }

    }

    /**
     * 유효성 검사 공통 사용 메소드
     * @param str
     * @return boolean
     */
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
