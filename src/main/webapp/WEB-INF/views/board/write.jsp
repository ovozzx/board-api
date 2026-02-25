<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/write.css">
</head>
<body>
	<form action="${pageContext.request.contextPath}/board/write" method="post" enctype="multipart/form-data">
		<div class="form-row">
			<select name="categoryId" class="required">
				<c:forEach var="category" items="${categoryList}">
					<option value="${category.categoryId}">${category.categoryName}</option>
				</c:forEach>
			</select>
		</div>
		<div class="form-row">
			<label for="create-user">작성자</label>
			<input type="text" id="create-user" name="createUser" class="required" value="${createUser}"/>
		</div>
		<div class="form-row">
			<label for="password">비밀번호</label>
			<input type="password" id="password" name="userPassword" placeholder="비밀번호" class="required" />
			<input type="password" id="password-confirm"  name="password-confirm" placeholder="비밀번호 확인" class="required" />
			<p id="password-error" class="error-msg" style="display:none;">
				비밀번호가 일치하지 않습니다.
			</p>
			<p id="msg" class="error-msg" style="display:none;">
				영문/숫자/특수문자 포함하여 4글자 이상 16글자 미만으로 입력해 주세요.
			</p>
		</div>
		<div class="form-row">
			<label for="title">제목</label>
			<input type="text" id="title" name="title" class="required" value="${title}"/>
		</div>
		<div class="form-row">
			<label for="content">내용</label>
			<textarea type="text" id="content" name="content" placeholder="내을 입력해 주세요." class="required">${content}</textarea>
		</div>
		<div class="form-row">
			<label for="attachment">파일 첨부</label>
			<div class="form-column">
				<!--같은 name이면 서버에서 List(또는 배열)로 받을 수 있음-->
				<input type="file" class="attachment" name="attachmentList" placeholder="내을 입력해 주세요." />
				<input type="file" class="attachment" name="attachmentList" placeholder="내을 입력해 주세요." />
				<input type="file" class="attachment" name="attachmentList" placeholder="내을 입력해 주세요." />
			</div>

		</div>
		<div class="button-group">
			<a class="move-list" href="${pageContext.request.contextPath}/board/list">취소</a>
			<button id="save-btn" type="submit" disabled>저장</button>
		</div>
	</form>
	<script>
		const requiredFields = document.querySelectorAll('.required');
		const saveBtn = document.getElementById('save-btn');
		const password = document.getElementById('password');
		const passwordConfirm = document.getElementById('password-confirm');
		const passwordError = document.getElementById('password-error');
		const msg = document.getElementById('msg');
		const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z\d]).{4,15}$/;

		function checkFields() {
			const allFilled = Array.from(requiredFields).every(el =>
					el.value.trim() !== ''
			);

			// 비밀번호 미일치 안내
			const isEqual = password.value.trim() === passwordConfirm.value.trim();
			if (!isEqual) {
				passwordError.style.display = 'block';
			}else{
				passwordError.style.display = 'none';
			}

			const isPassed = passwordRegex.test(password.value.trim());

			if (!isPassed) {
				msg.style.display = 'block';
			} else {
				msg.style.display = 'none';
			}

			saveBtn.disabled = !(allFilled && isEqual && isPassed);

		}

		// 최초 로딩 시 체크
		checkFields();

		requiredFields.forEach(el => {
			el.addEventListener('input', checkFields);
			el.addEventListener('change', checkFields);
		});
	</script>

</body>
</html>