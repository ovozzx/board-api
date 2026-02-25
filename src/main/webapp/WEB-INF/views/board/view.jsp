<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/view.css">
</head>
<body>
    <div id="modal" style="display: none;">
		<form action="${pageContext.request.contextPath}/board/delete" method="post">
			<input type="hidden" name="boardId" value="${board.boardId}">
			<label for="password">비밀번호</label>
			<input id="password" type="password" name="passwordInput" />
			<div class="modal-buttons">
				<button type="button" onclick="document.getElementById('modal').style.display='none'">취소</button>
				<button type="submit">확인</button>
			</div>
		</form>
	</div>
    <h1>게시판 - 보기</h1>
	<div class="board-info">
		<ul>
			<li>${board.createUser}</li>
			<li>등록일시 ${board.createDate}</li>
			<li>수정일시
				<c:choose>
					<c:when test="${not empty board.modifyDate}">
						${board.modifyDate}
					</c:when>
					<c:otherwise>
						-
					</c:otherwise>
				</c:choose>
			</li>
		</ul>
	</div>
	<div class="title">
		<ul>
			<li>[${board.categoryName}]</li>
			<li>${board.title}</li>
			<li>조회수: ${board.viewCount}</li>
		</ul>
	</div>
	<div class="content">
		${board.content}
	</div>
	<div class="attachment-area">
		<c:if test="${empty fileList}">
			<p>첨부파일 없음</p>
		</c:if>
		<c:if test="${not empty fileList}">
			<ul>
				<c:forEach var="file" items="${fileList}">
					<li>
						<a href="/board/download?fileId=${file.attachmentId}">
								${file.originalName}
						</a>
					</li>
				</c:forEach>
			</ul>
		</c:if>
	</div>
	<div class="reply-area">
        <c:forEach var="reply" items="${replyList}">
            <div>
                ${reply.createDate}
                ${reply.content}
            </div>
        </c:forEach>
		<form action="${pageContext.request.contextPath}/reply/write" method="post">
				<input type="hidden" name="boardId" value="${board.boardId}">
				<textarea type="text" name="content" placeholder="댓글을 입력해 주세요."></textarea>
				<button type="submit">등록</button>
		</form>
	</div>
	<div class="button-group">
		<a class="move-list" href="${pageContext.request.contextPath}/board/list">목록</a>
		<a class="modify" href="${pageContext.request.contextPath}/board/modify?boardId=${board.boardId}">수정</a>
		<button class="remove" onclick="document.getElementById('modal').style.display='block'">삭제</button>
	</div>
</body>
</html>

