package com.board.service;

import com.board.dto.*;
import com.board.mapper.BoardMapper;
import com.board.vo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.board.constants.BoardConstants.PAGE_GROUP_SIZE;
import static com.board.constants.BoardConstants.PAGE_SIZE;

@Service
public class BoardService {

    @Value("${board.upload-path}") // Spring이 application.properties에 있는 값을 꺼내서 변수에 넣어주는 어노테이션
    private String uploadPath;

    private final BoardMapper boardMapper; // mybatis

    public BoardService(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }

    public BoardsResponse getBoards(BoardsRequest boardsRequest) {

        SearchVO searchVO = SearchVO.from(boardsRequest);

        List<CategoryVO> categoryList = boardMapper.selectCategoryList();
        List<BoardVO> boardList = boardMapper.selectBoardList(searchVO);
        int boardListCount = boardMapper.selectBoardListCount(searchVO);

        // 페이지네이션 계산 => 생성 함수 만들기
        PageInfo pageInfo = createPageInfo(boardListCount, boardsRequest);

        BoardsResponse response = new BoardsResponse(categoryList, boardList, boardListCount, pageInfo);

        return response;
    }

    public BoardDetailResponse getDetailBoardById(int boardId) {

        BoardVO boardVO = boardMapper.selectBoard(boardId);
        List<ReplyVO> replyList = boardMapper.selectReplyList(boardId);
        List<AttachmentVO> fileList = boardMapper.selectFileList(boardId);

        BoardDetailResponse response = new BoardDetailResponse(boardVO, replyList, fileList);

        boardMapper.updateViewCount(boardId);

        return response;
    }

    // Docu 제거
    @Transactional
    public void registerBoard(BoardWriteRequest boardWriteRequest) throws IOException {

        // 게시글 insert (useGeneratedKeys로 boardId 자동 세팅)
        int insertBoardCnt = boardMapper.insertBoard(boardWriteRequest);

        // 게시글 insert 실패 == 첨부파일 저장 전 즉시 예외 던지기
        if (insertBoardCnt == 0) {
            throw new IllegalArgumentException("게시글 등록에 실패했습니다.");
        }

        int insertAttachmentCnt = 0;

        if (boardWriteRequest.getAttachmentList() != null){
            // 저장 경로 (프로젝트 외부 경로 권장)
            // 속성으로 관리. 배포 시스템마다 다름 (작업자마다 다름)
            // String uploadPath = request.getServletContext().getRealPath("/") + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            for (MultipartFile attachment : boardWriteRequest.getAttachmentList() ) { // MultipartFile : wrapper (바이너리 아님)
                if (attachment == null || attachment.isEmpty()) continue;
                if (insertAttachmentCnt >= 3) break; // 최대 3개

                String originalName = attachment.getOriginalFilename();
                String saveName = UUID.randomUUID() + "_" + originalName; // 중복 방지
                String ext = originalName.contains(".")
                        ? originalName.substring(originalName.lastIndexOf("."))
                        : "";

                // 파일 저장 (실제 파일을 디스크에 저장)
                attachment.transferTo(new File(uploadPath + File.separator + saveName));

                // DB insert
                AttachmentVO attachmentVO = new AttachmentVO(boardWriteRequest.getBoardId(), originalName, saveName, uploadPath, ext, attachment.getSize());

                boardMapper.insertAttachment(attachmentVO);
                insertAttachmentCnt++;
            }
        }
        // 성공이면 void, 실패면 throw (실패 원인 알려주기)

        // 첨부파일이 있는데 하나도 저장 못 한 경우
        if (boardWriteRequest.getAttachmentList() != null && insertAttachmentCnt == 0) {
            throw new IllegalArgumentException("첨부파일 저장에 실패했습니다.");
        }
    }

    @Transactional
    public void deleteBoard(BoardDeleteRequest requestBoardDelete) {
        String password = boardMapper.selectPasswordById(requestBoardDelete.getBoardId());

        if (password == null) { // 존재하지 않는 게시글
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        if (!password.equals(requestBoardDelete.getPasswordInput())) { // 비밀번호 틀렸을 경우
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        int successCnt = boardMapper.deleteBoard(requestBoardDelete.getBoardId());

        if(!(successCnt > 0)){
            throw new IllegalArgumentException("삭제 중 오류가 발생하였습니다.");
        }


    }

    public List<CategoryVO> getCategories() {
        return boardMapper.selectCategoryList();
    }

    public AttachmentVO getAttachmentById(int attachmentId) {
        return boardMapper.selectAttachmentById(attachmentId);
    }

    public BoardModifyResponse getModifyBoardById(int boardId) {
        BoardModifyResponse boardModifyVO = new BoardModifyResponse();

        boardModifyVO.setBoard(boardMapper.selectBoard(boardId));
        boardModifyVO.setFileList(boardMapper.selectFileList(boardId));

        return boardModifyVO;
    }

    @Transactional
    public void modifyBoard(BoardModifyRequest requestBoardModify) throws IOException {

        if (requestBoardModify.getPasswordInput() == null) {
            throw new IllegalArgumentException("비밀번호를 작성해 주세요.");
        }

        if (!requestBoardModify.getPasswordInput().equals(boardMapper.selectPasswordById(requestBoardModify.getBoardId()))) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 게시물 수정
        int updateCnt = boardMapper.updateBoard(requestBoardModify);

        // 게시글 수정 실패 == 첨부파일 수정 전 즉시 예외 던지기
        if(updateCnt == 0){
            throw new IllegalArgumentException("게시물 수정에 실패하였습니다."); // 잘못된 인자가 전달됐을 때 발생하는 예외
        }

        // 기존 첨부파일 논리 삭제
        if (requestBoardModify.getDeleteIds() != null) {
            for (int attachmentId : requestBoardModify.getDeleteIds() ) {
                boardMapper.deleteAttachment(attachmentId);
            }
        }

        int updateAttachmentCnt = 0;
        // 새 첨부파일 업로드
        if (requestBoardModify.getAttachmentList() != null) {
            int existingCount = boardMapper.selectFileList(requestBoardModify.getBoardId()).size();

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            for (MultipartFile file : requestBoardModify.getAttachmentList() ) {
                if (file == null || file.isEmpty()) continue;
                if (existingCount + updateAttachmentCnt >= 3) break;

                String originalName = file.getOriginalFilename();
                if (originalName == null) originalName = "unknown";
                String saveName = UUID.randomUUID() + "_" + originalName;
                String ext = originalName.contains(".")
                        ? originalName.substring(originalName.lastIndexOf(".")) : "";
                // 파일을 디스크에 저장하는 부분은 트랙잭션 롤백 대상이 아님
                file.transferTo(new File(uploadPath + File.separator + saveName));

                AttachmentVO attachmentVO = new AttachmentVO(requestBoardModify.getBoardId(), originalName, saveName, uploadPath, ext, file.getSize());

                boardMapper.insertAttachment(attachmentVO);
                updateAttachmentCnt++;
            }
        }

        if(requestBoardModify.getAttachmentList() != null && updateAttachmentCnt == 0){
            throw new IllegalArgumentException("첨부파일 수정에 실패하였습니다.");
        }
    }

    // 페이지네이션 함수
    public PageInfo createPageInfo(int boardListCount, BoardsRequest boardsRequest){
        int pageCount = (int) Math.ceil((double) boardListCount / PAGE_SIZE);
        if (pageCount == 0) pageCount = 1;
        int currentPage = (boardsRequest.getPage() == null || boardsRequest.getPage() < 1) ? 1 : boardsRequest.getPage();
        int startPage = ((currentPage - 1) / PAGE_GROUP_SIZE) * PAGE_GROUP_SIZE + 1;
        int endPage = Math.min(startPage + PAGE_GROUP_SIZE - 1, pageCount);
        PageInfo pageInfo = new PageInfo(currentPage, PAGE_SIZE, pageCount, startPage, endPage, boardListCount);
        return pageInfo;
    }

}