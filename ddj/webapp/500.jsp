<%@page isErrorPage="true" pageEncoding="UTF-8" %>
<%
    String basePath = request.getContextPath();
%>
<html>
<head>
    <title>页面出错</title>
    <style>
        .zbox {
            padding: 60px 55px;
            font-family: "微软雅黑";
            height: 300px;
            clear: both;
        }

        .zbox p {
            border: #e5e3c0 1px solid;
            padding: 23px 0px 23px 210px;
            line-height: 1.1;
            zoom: 1;
            background: #fcfbea;
            clear: both;
            overflow: hidden;
        }

        .zbox img {
            float: left;
            margin-right: 15px
        }

        .zbox a {
            color: blue;
            text-decoration: underline
        }

        .zbox strong {
            float: left;
            color: #000;
            font-size: 24px;
            padding-top: 10px
        }

        .zbox span {
            display: block;
            font-size: 14px;
            padding-top: 8px
        }
    </style>
</head>
<body>
<div class="zbox">
    <p>
        <img src="<%=basePath%>/html/system/css/images/error.png"><strong>出错了！</strong>
				<span>错误信息：<%=exception.getMessage() %><br>
				<a href="javascript:history.back();">后退</a>&nbsp;
				<a href="<%=basePath%>/" target="_parent">返回首页</a>
				</span>
    </p>
</div>
</body>
</html>