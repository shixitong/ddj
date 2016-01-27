<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set value="${pageContext.request.contextPath}" var="path"/>
<%--<c:set value="${path}/html/public/" var="resPath"/>--%>
<c:set value="${pageContext.request.contextPath}/html" var="resPath"/>
<%--<jsp:useBean id="CURRENT_DATE" class="java.util.Date" />--%>
<c:set value="1.0" var="scriptVersion"/>
