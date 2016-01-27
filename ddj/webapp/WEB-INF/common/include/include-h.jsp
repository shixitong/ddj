<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<c:set value="${pageContext.request.contextPath}" var="path"/>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="">
<meta http-equiv="description" content="">
<!-- css  -->
<link rel="stylesheet" type="text/css" href="${path}/html/easyui/themes/gray/easyui.css?ver=${scriptVersion}">
<link rel="stylesheet" type="text/css" href="${path}/html/easyui/themes/icon.css?ver=${scriptVersion}">
<link rel="stylesheet" type="text/css" href="${path}/html/system/css/common.css?ver=${scriptVersion}">
<link rel="shortcut icon" href="${path}/html/system/images/favicon.ico">

<script type="text/javascript" src="${path}/html/easyui/jquery.min.js?ver=${scriptVersion}"></script>
<script type="text/javascript" src="${path}/html/easyui/easyloader.js?ver=${scriptVersion}"></script>
<script type="text/javascript" src="${path}/html/easyui/jquery.easyui.min.js?ver=${scriptVersion}"></script>
<script type="text/javascript" src="${path}/html/easyui/locale/easyui-lang-zh_CN.js?ver=${scriptVersion}"></script>

<script type="text/javascript" src="${path }/html/system/js/json2.js?ver=${scriptVersion}"></script>
<script type="text/javascript" src="${path }/html/system/js/commons.js?ver=${scriptVersion}"></script>
<script type="text/javascript" src="${path }/html/system/js/app.js?ver=${scriptVersion}"></script>
<script type="text/javascript" src="${path}/html/system/js/extend.js?ver=${scriptVersion}"></script>
<script type="text/javascript">
    var CONTEXT_PATH = "${path}";
    $.ajaxSetup({contentType: 'application/x-www-form-urlencoded; charset=UTF-8'});
    /*,complete: onRequestCompleted
    function onRequestCompleted(xhr,textStatus) {
        //alert(xhr.status+"--"+textStatus);
        if (xhr.status == 0 && textStatus == "error") {
            //location.href = xhr.getResponseHeader("Location");
            console.log(xhr.statusCode());
            console.log(xhr.getResponseHeader());
        }
    }*/
</script>