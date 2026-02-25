package com.board.service;

import com.board.dto.*;
import com.board.mapper.BoardMapper;
import com.board.vo.*;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.board.constants.BoardConstants.PAGE_GROUP_SIZE;
import static com.board.constants.BoardConstants.PAGE_SIZE;
import static com.board.constants.BoardConstants.UPLOAD_PATH;

@Service
public class BoardService {

    private final BoardMapper boardMapper;

    public BoardService(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }

    /**
     * 전체 카테고리 및 게시글 조회
     * @param searchVO
     * @return BoardListViewVO
     */
    public ResponseBoardList getBoardListView(RequestBoardList requestBoardList) {

        SearchVO searchVO = new SearchVO(); // map
        searchVO.setStartDate(requestBoardList.getStartDate());
        searchVO.setEndDate(requestBoardList.getEndDate());
        searchVO.setCategoryId(requestBoardList.getCategoryId());
        searchVO.setKeyword(requestBoardList.getKeyword());
        searchVO.setPage(requestBoardList.getPage());
        // TODO : 위 반복되는 부분 mapper => map struct 사용

        List<CategoryVO> categoryList = boardMapper.selectCategoryList();
        List<BoardVO> boardList = boardMapper.selectBoardList(searchVO);
        int boardListCount = boardMapper.selectBoardListCount(searchVO);

        // 페이지네이션 계산 => 생성 함수 만들기
        int pageCount = (int) Math.ceil((double) boardListCount / PAGE_SIZE);
        if (pageCount == 0) pageCount = 1;
        int currentPage = requestBoardList.getPage() < 1 ? 1 : requestBoardList.getPage();
        int startPage = ((currentPage - 1) / PAGE_GROUP_SIZE) * PAGE_GROUP_SIZE + 1;
        int endPage = Math.min(startPage + PAGE_GROUP_SIZE - 1, pageCount);
        PageInfo pageInfo = new PageInfo(currentPage, PAGE_SIZE, pageCount, startPage, endPage, boardListCount);

        ResponseBoardList response = new ResponseBoardList();
        response.setCategoryList(categoryList);
        response.setBoardList(boardList);
        response.setBoardListCount(boardListCount);
        response.setPageInfo(pageInfo);

        return response;
    }

    /**
     * 상세 게시글 조회
     * @param boardId
     * @return BoardDetailViewVO
     */
    public ResponseBoardDetail getDetailBoardById(String boardId) {
        ResponseBoardDetail boardDetailViewVO = new ResponseBoardDetail();

        BoardVO boardVO = boardMapper.selectBoard(boardId);
        List<ReplyVO> replyList = boardMapper.selectReplyList(boardId);
        List<AttachmentVO> fileList = boardMapper.selectFileList(boardId);

        boardDetailViewVO.setBoard(boardVO);
        boardDetailViewVO.setReplyList(replyList);
        boardDetailViewVO.setFileList(fileList);

        boardMapper.updateViewCount(boardId);

        return boardDetailViewVO;
    }

    /**
     * 게시글 및 첨부파일 등록하기
     * @param board
     * @param attachmentList
     * @return boolean
     * @throws IOException
     */
    // TODO : Docu 제거
    //
    public boolean registerBoard(RequestBoardWrite requestBoardWrite) throws IOException {

        // 게시글 insert (useGeneratedKeys로 boardId 자동 세팅)
        int insertBoardCnt = boardMapper.insertBoard(requestBoardWrite);

        if (requestBoardWrite.getAttachmentList() == null) return insertBoardCnt > 0;

        // 저장 경로 (프로젝트 외부 경로 권장)
        String uploadPath = UPLOAD_PATH; // TODO : 속성으로 관리. 배포 시스템마다 다름 (작업자마다 다름)
        // String uploadPath = request.getServletContext().getRealPath("/") + "uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        int count = 0;
        for (MultipartFile attachment : requestBoardWrite.getAttachmentList() ) { // MultipartFile : wrapper (바이너리 아님)
            if (attachment == null || attachment.isEmpty()) continue;
            if (count >= 3) break; // 최대 3개

            String originalName = attachment.getOriginalFilename();
            String saveName = UUID.randomUUID() + "_" + originalName; // 중복 방지
            String ext = originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";

            // 파일 저장 (실제 파일을 디스크에 저장)
            attachment.transferTo(new File(uploadPath + File.separator + saveName));

            // DB insert
            AttachmentVO attachmentVO = new AttachmentVO();
            attachmentVO.setBoardId(requestBoardWrite.getBoardId());
            attachmentVO.setOriginalName(originalName);
            attachmentVO.setSaveName(saveName);
            attachmentVO.setFilePath(uploadPath);
            attachmentVO.setFileExt(ext);
            attachmentVO.setFileSize(attachment.getSize());

            boardMapper.insertAttachment(attachmentVO);
            count++;
        }
        // TODO : 등록 -> false
        // 성공이면 void, 실패면 throw (실패 원인 알려주기)
        return insertBoardCnt > 0 && count > 0; // 게시글 & 첨부파일 성공
    }

    /**
     * 비밀번호 검증 후 삭제하기
     * @param deleteBoardVO
     * @return boolean
     */
    @Transactional
    public boolean deleteBoard(RequestBoardDelete requestBoardDelete) {
        String password = boardMapper.selectPasswordById(requestBoardDelete.getBoardId());

        if (password == null) { // 존재하지 않는 게시글
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        if (!password.equals(requestBoardDelete.getPasswordInput())) { // 비밀번호 틀렸을 경우
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        int successCnt = boardMapper.deleteBoard(requestBoardDelete.getBoardId());

        if(successCnt > 0){
            return successCnt > 0;
        }else{ // 삭제 중 오류
            throw new IllegalArgumentException("삭제 중 오류가 발생하였습니다.");
        }


    }

    /**
     * 카테고리 읽기
     * @return List<CategoryVO>
     */
    public List<CategoryVO> getCategoryList() {
        return boardMapper.selectCategoryList();
    }

    /**
     * 첨부파일 읽기
     * @param attachmentId
     * @return AttachmentVO
     */
    public AttachmentVO getAttachmentById(String attachmentId) {
        return boardMapper.selectAttachmentById(attachmentId);
    }

    /**
     * 수정화면 데이터 읽기
     * @param boardId
     * @return BoardModifyVO
     */
    public ResponseBoardModify getModifyBoardById(String boardId) {
        ResponseBoardModify boardModifyVO = new ResponseBoardModify();

        boardModifyVO.setBoard(boardMapper.selectBoard(boardId));
        boardModifyVO.setFileList(boardMapper.selectFileList(boardId));

        return boardModifyVO;
    }

    /**
     * 비밀번호 검증 후 게시글 수정하기
     * @param passwordInput
     * @param board
     * @param deleteIds
     * @param newFiles
     * @return
     * @throws IOException
     */
    @Transactional
    public String modifyBoard(RequestBoardModify requestBoardModify) throws IOException {

        if (requestBoardModify.getPasswordInput() == null) {
            throw new IllegalArgumentException("비밀번호를 작성해 주세요.");
        }

        if (!requestBoardModify.getPasswordInput().equals(boardMapper.selectPasswordById(requestBoardModify.getBoardId()))) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 게시물 수정
        boardMapper.updateBoard(requestBoardModify);

        // 기존 첨부파일 논리 삭제
        if (requestBoardModify.getDeleteIds() != null) {
            for (String attachmentId : requestBoardModify.getDeleteIds() ) {
                boardMapper.deleteAttachment(attachmentId);
            }
        }

        // 새 첨부파일 업로드
        if (requestBoardModify.getAttachmentList() != null) {
            int existingCount = boardMapper.selectFileList(requestBoardModify.getBoardId()).size();
            int count = 0;

            String uploadPath = UPLOAD_PATH;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            for (MultipartFile file : requestBoardModify.getAttachmentList() ) {
                if (file == null || file.isEmpty()) continue;
                if (existingCount + count >= 3) break;

                String originalName = file.getOriginalFilename();
                if (originalName == null) originalName = "unknown";
                String saveName = UUID.randomUUID() + "_" + originalName;
                String ext = originalName.contains(".")
                        ? originalName.substring(originalName.lastIndexOf(".")) : "";

                file.transferTo(new File(uploadPath + File.separator + saveName));

                AttachmentVO attachmentVO = new AttachmentVO();
                attachmentVO.setBoardId(requestBoardModify.getBoardId());
                attachmentVO.setOriginalName(originalName);
                attachmentVO.setSaveName(saveName);
                attachmentVO.setFilePath(uploadPath);
                attachmentVO.setFileExt(ext);
                attachmentVO.setFileSize(file.getSize());

                boardMapper.insertAttachment(attachmentVO);
                count++;
            }
        }

        return "수정 완료되었습니다.";
    }
}