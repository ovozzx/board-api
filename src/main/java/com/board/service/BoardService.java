package com.board.service;

import com.board.api.BoardController;
import com.board.dto.*;
import com.board.exception.BadRequestException;
import com.board.exception.NotFoundException;
import com.board.exception.PasswordMismatchException;
import com.board.mapper.BoardMapper;
import com.board.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import static com.board.constants.BoardConstants.MAX_ATTACHMENT_COUNT;



@Service
public class BoardService {

    private static final Logger log = LoggerFactory.getLogger(BoardService.class);
    private final PasswordEncoder passwordEncoder;
    @Value("${board.upload-path}") // Spring이 application.properties에 있는 값을 꺼내서 변수에 넣어주는 어노테이션
    private String uploadPath;

    private final BoardMapper boardMapper; // mybatis

    public BoardService(BoardMapper boardMapper, PasswordEncoder passwordEncoder) {
        this.boardMapper = boardMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public BoardsResponse getBoards(SearchVO searchVO) {

        List<CategoryVO> categoryList = boardMapper.selectCategoryList();
        List<BoardVO> boardList = boardMapper.selectBoardList(searchVO);
        int boardListCount = boardMapper.selectBoardListCount(searchVO);

        BoardsResponse response = new BoardsResponse(categoryList, boardList, boardListCount, searchVO);

        return response;
    }

    public BoardDetailResponse getBoard(int boardId) {
        // 서비스 입장에서는 어떤 화면인지까지 고려하지 않도록
        BoardVO boardVO = boardMapper.selectBoard(boardId);
        List<AttachmentVO> fileList = boardMapper.selectFileList(boardId);

        BoardDetailResponse response = new BoardDetailResponse(boardVO, null, fileList);

        return response;
    }

    public List<ReplyVO> getReplyList(int boardId){
        List<ReplyVO> replyList = boardMapper.selectReplyList(boardId);
        return replyList;
    }

    public void updateViewCount(int boardId) {

        // TODO : 예외에 대한 처리 어떻게 할지. 안 올라가게 함
        try{
            boardMapper.updateViewCount(boardId);
        } catch(Exception e){
            log.error("조회 수 증가 실패 boardId = {}", boardId, e);
        }

    }

    @Transactional
    public void registerBoard(BoardVO boardVO) throws IOException {

        // 패스워드 해싱 처리
        boardVO.setUserPassword(passwordEncoder.encode(boardVO.getUserPassword()));

        // 게시글 insert (useGeneratedKeys로 boardId 자동 세팅)
        int insertBoardCnt = boardMapper.insertBoard(boardVO);

        // 게시글 insert 실패 == 첨부파일 저장 전 즉시 예외 던지기
        if (insertBoardCnt == 0) {
             new RuntimeException("게시글 등록에 실패했습니다."); // 서버쪽 문제 -> 500
        }

        int insertAttachmentCnt = 0;

        if (boardVO.getAttachmentList() != null){
            // 저장 경로 (프로젝트 외부 경로 권장)
            // 속성으로 관리. 배포 시스템마다 다름 (작업자마다 다름)
            // String uploadPath = request.getServletContext().getRealPath("/") + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            for (MultipartFile attachment : boardVO.getAttachmentList() ) { // MultipartFile : wrapper (바이너리 아님)
                if (attachment == null || attachment.isEmpty()) continue;
                if (insertAttachmentCnt >= MAX_ATTACHMENT_COUNT) break; // 최대 10개

                String originalName = attachment.getOriginalFilename();
                String saveName = UUID.randomUUID() + "_" + originalName; // 중복 방지
                String ext = originalName.contains(".")
                        ? originalName.substring(originalName.lastIndexOf("."))
                        : "";

                // 파일 저장 (실제 파일을 디스크에 저장)
                attachment.transferTo(new File(uploadPath + File.separator + saveName));

                // DB insert
                AttachmentVO attachmentVO = new AttachmentVO(boardVO.getBoardId(), originalName, saveName, uploadPath, ext, attachment.getSize());

                boardMapper.insertAttachment(attachmentVO);
                insertAttachmentCnt++;
            }
        }
        // 성공이면 void, 실패면 throw (실패 원인 알려주기)

        // 첨부파일이 있는데 하나도 저장 못 한 경우
        if (boardVO.getAttachmentList() != null && insertAttachmentCnt == 0) {
            throw new RuntimeException("첨부파일 저장에 실패했습니다.");
        }
    }
    // check uncheck
    // TODO : 500도 따로 빼기. 예외 이름만 봐도 파악 가능하도록 -> 후처리
    // 게시판은 트랜잭션 필요 없음 --> 목적이 있어야 함 (부하 있음)
    @Transactional
    public void deleteBoard(int boardId, String passwordInput) {
        String password = boardMapper.selectPasswordById(boardId);

        if (password == null) { // 존재하지 않는 게시글
            throw new NotFoundException("존재하지 않는 게시글입니다.");
        }

        if (!passwordEncoder.matches(passwordInput, password)) { // 비밀번호 틀렸을 경우
            //  matches(평문, 해시) => 해시 안 salt를 꺼내 사용해서 평문을 해싱 후 비교
            throw new PasswordMismatchException("비밀번호가 틀렸습니다.");
        }

        int successCnt = boardMapper.deleteBoard(boardId);

        if(!(successCnt > 0)){
            throw new RuntimeException("삭제 중 오류가 발생하였습니다.");
        }


    }

    public List<CategoryVO> getCategories() {
        return boardMapper.selectCategoryList();
    }

    public AttachmentVO getAttachmentById(int attachmentId) {
        return boardMapper.selectAttachmentById(attachmentId);
    }

    @Transactional
    public void modifyBoard(BoardVO boardVO) throws IOException {

        if (boardVO.getUserPassword() == null) {
            throw new BadRequestException("비밀번호를 작성해 주세요."); // 필수값 누락 -> 400
        }

        String password = boardMapper.selectPasswordById(boardVO.getBoardId());

        if (!passwordEncoder.matches(boardVO.getUserPassword(), password)) {
            //  matches(평문, 해시) => 해시 안 salt를 꺼내 사용해서 평문을 해싱 후 비교
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        // 게시물 수정
        int updateCnt = boardMapper.updateBoard(boardVO);

        // 게시글 수정 실패 == 첨부파일 수정 전 즉시 예외 던지기
        if(updateCnt == 0){
            throw new RuntimeException("게시물 수정에 실패하였습니다."); // 잘못된 인자가 전달됐을 때 발생하는 예외
        }

        // 기존 첨부파일 논리 삭제
        if (boardVO.getDeleteIds() != null) {
            for (int attachmentId : boardVO.getDeleteIds() ) {
                boardMapper.deleteAttachment(attachmentId);
            }
        }

        int updateAttachmentCnt = 0;
        // 새 첨부파일 업로드
        if (boardVO.getAttachmentList() != null) {
            int existingCount = boardMapper.selectFileList(boardVO.getBoardId()).size();

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            for (MultipartFile file : boardVO.getAttachmentList() ) {
                if (file == null || file.isEmpty()) continue;
                if (existingCount + updateAttachmentCnt >= 3) break;

                String originalName = file.getOriginalFilename();
                if (originalName == null) originalName = "unknown";
                String saveName = UUID.randomUUID() + "_" + originalName;
                String ext = originalName.contains(".")
                        ? originalName.substring(originalName.lastIndexOf(".")) : "";
                // 파일을 디스크에 저장하는 부분은 트랙잭션 롤백 대상이 아님
                file.transferTo(new File(uploadPath + File.separator + saveName));

                AttachmentVO attachmentVO = new AttachmentVO(boardVO.getBoardId(), originalName, saveName, uploadPath, ext, file.getSize());

                boardMapper.insertAttachment(attachmentVO);
                updateAttachmentCnt++;
            }
        }

        if(boardVO.getAttachmentList() != null && updateAttachmentCnt == 0){
            throw new RuntimeException("첨부파일 수정에 실패하였습니다.");
        }
    }


    public void registerReply(ReplyVO replyVO) {
        int insertCnt = boardMapper.insertReply(replyVO);
        if(insertCnt == 0){
            throw new RuntimeException("댓글 등록에 실패하였습니다.");
        }
    }



}