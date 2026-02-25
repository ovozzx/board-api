<%--
  Created by IntelliJ IDEA.
  User: leesoyeong
  Date: 26. 1. 28.
  Time: 오후 6:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <script>
        alert("${alertMsg}");
        <c:choose>
            <c:when test="${redirectUrl == ''}">
                history.back();
            </c:when>
            <c:otherwise>
                location.href = "${pageContext.request.contextPath}${redirectUrl}";
            </c:otherwise>
        </c:choose>
    </script>
</body>
</html>
