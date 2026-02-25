package com.board.controller;

import com.board.dto.ResponseBoardList;
import com.board.service.BoardService;
import com.board.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList; // 없어야 됨
import java.util.List;

import static com.board.constants.BoardConstants.PAGE_GROUP_SIZE;
import static com.board.constants.BoardConstants.PAGE_SIZE;

// TODO : 다음에 할 것 => rest api, react
/**
 * 롬복, map structure 사용 필수
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
// 페이지네이션 & 수정 첨부파일
@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService service;

    /**
     * 게시글 목록 조회 (REST API)
     * @param searchVO : 검색 조건, 페이지
     * @return BoardListResponseVO
     * TODO : 롬복 : getter, setter 자동 생성 쓰기
     * dto : Data Transfer Object => 래핑해서 넘기자 구체적인 용도 (vo가 더 큰 범위)
     * 용도별 vo 구성 달라지는 거라서 dto는 용도별로 다르게 생길수밖에 없음
     */


//    @GetMapping("/list")
//    public ResponseEntity<BoardListResponseVO> getBoardList(SearchVO searchVO) {
//        // 검색 날짜 조건 default 설정
//        LocalDate today = LocalDate.now();
//
//        if (searchVO.getStartDate() == null || searchVO.getStartDate().isEmpty()) {
//            searchVO.setStartDate(today.minusYears(1).toString());
//        }
//
//        if (searchVO.getEndDate() == null || searchVO.getEndDate().isEmpty()) {
//            searchVO.setEndDate(today.toString());
//        }
//
//        // 서비스 호출
//        ResponseBoardList boardListViewVO = service.getBoardListView(searchVO);
//        int allBoardCount = boardListViewVO.getBoardListCount();
//        int pageCount = (int) Math.ceil((double) allBoardCount / PAGE_SIZE);
//        if (pageCount == 0) pageCount = 1;
//
//        // 페이지네이션 계산
//        int currentPage = searchVO.getPage() < 1 ? 1 : searchVO.getPage();
//        int pageGroupSize = PAGE_GROUP_SIZE;
//        int startPage = ((currentPage - 1) / pageGroupSize) * pageGroupSize + 1;
//        int endPage = Math.min(startPage + pageGroupSize - 1, pageCount);
//
//        // Response VO 구성
//        PageInfo pageInfo = new PageInfo(currentPage, PAGE_SIZE, pageCount, startPage, endPage, allBoardCount);
//
//        BoardListResponseVO response = new BoardListResponseVO();
//        response.setCategoryList(boardListViewVO.getCategoryList());
//        response.setBoardList(boardListViewVO.getBoardList());
//        response.setPageInfo(pageInfo);
//        response.setSearchCondition(searchVO);
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * 게시글 상세 보기
//     * @param boardId
//     * @param model
//     * @return view
//     */
//    @GetMapping("/view")
//    public String getBoardDetailById(@RequestParam(required = false) String boardId, Model model){
//
//        BoardDetailViewVO boardDetailViewVO = service.getDetailBoardById(boardId);
//
//        model.addAttribute("replyList", boardDetailViewVO.getReplyList());
//        model.addAttribute("fileList", boardDetailViewVO.getFileList());
//        model.addAttribute("board", boardDetailViewVO.getBoard());
//
//        return "board/view";
//    }
//
//    /***
//     * 게시글 작성 화면
//     * @param model
//     * @return view
//     */
//    @GetMapping("/write")
//    public String viewWritePage(Model model){
//        List<CategoryVO> categoryList = service.getCategoryList();
//        model.addAttribute("categoryList", categoryList);
//        return "board/write";
//    }
//
//    /**
//     * 게시글 수정 화면
//     * @param boardId
//     * @param model
//     * @return view
//     */
//    @GetMapping("/modify")
//    public String viewModifyPage(@RequestParam(required = false) String boardId, Model model){
//        System.out.println("===" + boardId);
//        // 수정 시에는 조회수 미증가
//        BoardModifyVO boardModifyVO = service.getModifyBoardById(boardId);
//
//        model.addAttribute("fileList", boardModifyVO.getFileList());
//        model.addAttribute("board", boardModifyVO.getBoard());
//
//        return "board/modify";
//    }
//
//    /**
//     * 첨부파일 다운로드
//     * @param fileId
//     * @return ResponseEntity
//     * @throws IOException
//     */
//    @GetMapping("/download")
//    public ResponseEntity<Resource> downloadFile(@RequestParam String fileId) throws IOException {
//        AttachmentVO attachment = service.getAttachmentById(fileId);
//
//        if (attachment == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        File file = new File(attachment.getFilePath() + File.separator + attachment.getSaveName());
//
//        if (!file.exists()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Resource resource = new FileSystemResource(file); // 디스크의 파일을 HTTP 응답 바디로 스트리밍할 수 있게 래핑
//        String encodedName = URLEncoder.encode(attachment.getOriginalName(), "UTF-8")
//                                       .replaceAll("\\+", "%20");
//        // TODO : 통으로 읽어서 내려받기 or 부분 부분만 내려받기
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
//                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
//                .body(resource); // 실제 파일 데이터를 응답 바디에 실어서 전송
//    }
//
//    /**
//     * 게시글 등록하기
//     * @param board
//     * @param attachmentList
//     * @return view
//     * @throws IOException
//     */
//    @PostMapping("/write")
//    public String registerBoard(BoardVO board
//    , @RequestParam(required = false) List<MultipartFile> attachmentList
//                                ) throws IOException {
//        System.out.println(board);
//        System.out.println(attachmentList);
//
//        // 유효성 : 작성자, 비밀번호, 제목, 내용
//        boolean isInvalid =
//                        isBlank(board.getCreateUser()) ||
//                        isBlank(board.getUserPassword()) ||
//                        isBlank(board.getTitle()) ||
//                        isBlank(board.getContent());
//
//        if (isInvalid) {
//            return "board/write";
//        }
//
//        boolean isRegistered = service.registerBoard(board, attachmentList);
//        if(isRegistered){
//            return "redirect:/board/list";
//        } else{
//            return "board/write"; // 등록 실패 안내
//        }
//    }
//
//    /**
//     * 게시글 수정하기
//     * @param passwordInput
//     * @param board
//     * @param deleteIds
//     * @param newFiles
//     * @param model
//     * @param redirectAttributes
//     * @return view
//     * @throws IOException
//     */
//    @PostMapping("/modify")
//    public String modifyBoard(
//            @RequestParam String passwordInput,
//            BoardVO board,
//            @RequestParam(required = false) List<String> deleteIds,
//            @RequestParam(required = false) List<MultipartFile> newFiles,
//            Model model,
//            RedirectAttributes redirectAttributes) throws IOException {
//        // TODO : 코드 분석
//
//        boolean isInvalid =
//                isBlank(board.getCreateUser()) ||
//                        isBlank(passwordInput) ||
//                        isBlank(board.getTitle()) ||
//                        isBlank(board.getContent());
//
//        if (isInvalid) {
//            redirectAttributes.addFlashAttribute("alertMsg", "필수값이 누락되었습니다.");
//            return "redirect:/board/modify?boardId=" + board.getBoardId();
//        }
//
//        try{
//            service.modifyBoard(passwordInput, board, deleteIds, newFiles);
//        }catch(IllegalArgumentException e){
//            redirectAttributes.addFlashAttribute("alertMsg", e.getMessage());
//            return "redirect:/board/modify?boardId=" + board.getBoardId();
//        }
//
//        return "redirect:/board/view?boardId=" + board.getBoardId();
//    }
//
//    /**
//     * 게시글 삭제하기
//     * @param deleteBoardVO : passwordInput, boardId
//     * @param model
//     * @return view
//     * @throws IOException
//     */
//    @PostMapping("/delete")
//    public String deleteBoard(DeleteBoardVO deleteBoardVO, Model model) throws IOException {
//        System.out.println("===" + deleteBoardVO.getPasswordInput());
//        // 이 흐름이 맞음
//        // 컨트롤러 말고 global 핸들러에서 하도록 (한곳에서 처리)
//        // 첨부파일, 댓글이 있는 게시물 삭제! => 실제 바이너리 삭제는 어떻게 할지 (정책에 따라)
//        // 삭제 시, 본문 글만 지우는 경우 등 여러 케이스 존재 (고민).. cascade
//        try{
//            service.deleteBoard(deleteBoardVO); // 결과 cnt 피하기
//            model.addAttribute("alertMsg", "삭제가 완료되었습니다.");
//            model.addAttribute("redirectUrl", "/board/list");
//            return "board/alert";
//        }catch (IllegalArgumentException e){
//            // 오류 alert
//            model.addAttribute("alertMsg", e.getMessage());
//            model.addAttribute("redirectUrl", "");
//            return "board/alert";
//        }
//
//    }
//
//    /**
//     * 유효성 검사 공통 사용 메소드
//     * @param str
//     * @return boolean
//     */
//    private boolean isBlank(String str) {
//        return str == null || str.trim().isEmpty();
//    }
}
