<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Insert title here</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/modify.css">
	<script src="${pageContext.request.contextPath}/js/modify.js"></script>
</head>
<body>
<h1>게시판 - 수정</h1>

<form action="${pageContext.request.contextPath}/board/modify" method="post" enctype="multipart/form-data">
	<input type="hidden" name="boardId" value="${board.boardId}">

	<div class="form-row">
		<label>카테고리</label>
		<input type="text" name="categoryName" value="${board.categoryName}" readonly />
	</div>

	<div class="form-row">
		<label>등록 일시</label>
		<input type="text" name="createDate" value="${board.createDate}" readonly />
	</div>

	<div class="form-row">
		<label>수정 일시</label>
		<input type="text" name="modifyDate"
			   value=
			   <c:choose>
				   <c:when test="${not empty board.modifyDate}">
					   ${board.modifyDate}
				   </c:when>
				   <c:otherwise>
						   -
				   </c:otherwise>
			   </c:choose>
	    readonly />
	</div>

	<div class="form-row">
		<label>조회수</label>
		<input type="text" name="viewCount" value="${board.viewCount}" readonly />
	</div>

	<div class="form-row">
		<label for="create-user">작성자</label>
		<input type="text"
			   id="create-user"
			   name="createUser"
			   class="required"
			   value="${not empty createUser ? createUser : board.createUser}">
	</div>

	<div class="form-row">
		<label for="password">비밀번호</label>
		<input type="password"
			   id="password"
			   name="passwordInput"
			   class="required">

		<c:if test="${not empty alertMsg}">
			<p id="password-error" class="error-msg">
					${alertMsg}
			</p>
		</c:if>
	</div>

	<div class="form-row">
		<label for="title">제목</label>
		<input type="text"
			   id="title"
			   name="title"
			   class="required"
			   value="${not empty title ? title : board.title}">
	</div>

	<div class="form-row">
		<label for="content">내용</label>
		<textarea id="content"
				  name="content"
				  class="required">${not empty content ? content : board.content}</textarea>
	</div>
	<!-- 기존 파일 x 버튼 클릭 시, 파일 선택으로 바뀌고 x 버튼 사라짐 -->
	<ul id="existing-files">
		<c:forEach var="file" items="${fileList}">
			<li data-attachment-id="${file.attachmentId}">
				<a href="/board/download?fileId=${file.attachmentId}">
						${file.originalName}
				</a>
				<span class="delete-btn">X</span>
			</li>
		</c:forEach>
	</ul>

	<div id="deleted-files-area"></div>

	<div class="form-column" id="file-input-area"></div>

	<div class="button-group">
		<a href="${pageContext.request.contextPath}/board/view?boardId=${board.boardId}">취소</a>
		<button id="save-btn" type="submit" disabled>저장</button>
	</div>
</form>
</body>
</html>
