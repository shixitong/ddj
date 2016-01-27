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
		<%--<link href="${path }/html/system/css/default/conf.css?ver=${scriptVersion}" type="text/css" rel="stylesheet"/>--%>
        <link href="${path }/html/system/js/easyui/themes/icon.css?ver=${scriptVersion}" type="text/css" rel="stylesheet"/>
        <link href="${path }/html/system/js/easyui/themes/gray/easyui.css?ver=${scriptVersion}" type="text/css" rel="stylesheet"/>

        <!-- easyUI  -->
        <script type="text/javascript" src="${path}/html/system/js/jquery-1.7.1.js?ver=${scriptVersion}"></script>
        <!-- constant -->
        <script type="text/javascript">
            /******
             * 获取上下文路径
             * @return {String}
             */
            var CONTEXT_PATH = "${path}";

            //ajax全局设置
            $.ajaxSetup({contentType: 'application/x-www-form-urlencoded; charset=UTF-8'})

        </script>
        <script type="text/javascript" src="${path }/html/jquery/jquery.cookie.js" charset="utf-8"></script>
        <%--<link id="easyuiTheme" rel="stylesheet" href="${path }/html/system/js/easyui/themes/gray/easyui.css?ver=${scriptVersion}" type="text/css"/>--%>
        <script type="text/javascript" src="${path }/html/system/js/easyui/easyloader.js?ver=${scriptVersion}"></script>
        <script type="text/javascript" src="${path}/html/system/js/easyui/jquery.easyui.min.js?ver=${scriptVersion}"></script>
        <script type="text/javascript" src="${path }/html/system/js/easyui/easyui-lang-zh_CN.js?ver=${scriptVersion}"></script>
        <!-- conf -->
        <script type="text/javascript" src="${path }/html/system/js/json2.js?ver=${scriptVersion}"></script>
        <script type="text/javascript" src="${path }/html/system/js/commons.js?ver=${scriptVersion}"></script>
        <script type="text/javascript" src="${path }/html/system/js/app.js?ver=${scriptVersion}"></script>
        <script type="text/javascript" src="${path}/html/edm/js/extend.js?ver=${scriptVersion}"></script>
