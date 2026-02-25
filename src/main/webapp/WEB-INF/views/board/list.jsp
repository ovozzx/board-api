<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/list.css">
</head>
<body>
	<div class="wrapper">
    	<h1>자유 게시판 - 목록</h1>
		<form action="${pageContext.request.contextPath}/board/list" method="get">
			<ul class="top-bar">
<%--				<input name="page" type="hidden" />--%>
				<li>등록일</li>
				<li>
					<input name="startDate" type="date" value="${searchVO.startDate}"/>
					~
					<input name="endDate" type="date" value="${searchVO.endDate}"/>
				</li>
				<li>
					<select name="categoryId">
						<option value="0">전체 카테고리</option>
						<c:forEach var="category" items="${categoryList}">
						<option value="${category.categoryId}">${category.categoryName}</option>
						</c:forEach>
					</select>

					<input name="keyword" type="text" class="search-input"
						   placeholder="검색어를 입력해 주세요. (제목+작성자+내용)"
						   value="${param.keyword}" />
				</li>
				<li>
					<button type="submit" class="search-btn">검색</button>
				</li>
			</ul>
		</form>
		<div class="count-bar">총  ${boardListCount} 건</div>
		<table class="list">
			<thead>
				<tr>
					<th>카테고리</th>
					<th>제목</th>
					<th>작성자</th>
					<th>조회수</th>
					<th>등록 일시</th>
					<th>수정 일시</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="board" items="${boardList}"> <!-- req에 담겨있어서 쓸 수 있음 -->
					<tr>
						<td>${board.categoryName}</td>
						<td>
							<c:if test="${board.hasAttachment}">
								<img src="https://cdn-icons-png.freepik.com/512/8455/8455362.png">
							</c:if>
							<a class="title" href="view?boardId=${board.boardId}">
								<c:choose>
									<c:when test="${fn:length(board.title) > 80}">
										${fn:substring(board.title, 0, 80)}...
									</c:when>
									<c:otherwise>
										${board.title}
									</c:otherwise>
								</c:choose>
							</a>
						</td>
						<td>${board.createUser}</td>
						<td>${board.viewCount}</td>
						<td>${board.createDate}</td>
						<td>
							<c:choose>
								<c:when test="${not empty board.modifyDate}">
									${board.modifyDate}
								</c:when>
								<c:otherwise>
									-
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<!-- 페이지네이션 -->
		<div class="pagination">
			<!-- 맨 처음으로 이동 -->
			<a href="?page=1&startDate=${searchVO.startDate}&endDate=${searchVO.endDate}&categoryId=${searchVO.categoryId}&keyword=${searchVO.keyword}">&lt;&lt;</a>
			<!-- 앞 페이지로 이동 -->
			<a href="?page=${currentPage - 1 < 1 ? 1 : currentPage - 1}&startDate=${searchVO.startDate}&endDate=${searchVO.endDate}&categoryId=${searchVO.categoryId}&keyword=${searchVO.keyword}">&lt;</a>
			<c:forEach begin="${startPage}" end="${endPage}" var="i">
				<a href="?page=${i}&startDate=${searchVO.startDate}&endDate=${searchVO.endDate}&categoryId=${searchVO.categoryId}&keyword=${searchVO.keyword}"
				   class="${i == currentPage ? 'active' : ''}">${i}</a>
			</c:forEach>
			<!-- 뒤 페이지로 이동 -->
			<a href="?page=${currentPage + 1 > pageCount ? pageCount : currentPage + 1}&startDate=${searchVO.startDate}&endDate=${searchVO.endDate}&categoryId=${searchVO.categoryId}&keyword=${searchVO.keyword}">&gt;</a>
			<!-- 맨 뒤로 이동 -->
			<a href="?page=${pageCount}&startDate=${searchVO.startDate}&endDate=${searchVO.endDate}&categoryId=${searchVO.categoryId}&keyword=${searchVO.keyword}">&gt;&gt;</a>
		</div>
		<div class="bottom-bar">
			<a class="write" href="write">등록</a>
		</div>
	</div>
</body>
</html>

