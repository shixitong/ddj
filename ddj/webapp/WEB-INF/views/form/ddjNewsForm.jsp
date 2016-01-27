<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/common/include.jsp" %>

<%--表单流程绑定 End--%>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@ include file="/WEB-INF/haf/html-header.jsp" %>
    <%@ include file="/WEB-INF/haf/form-html-header.jsp" %>
    <%@ include file="/WEB-INF/haf/html-kindEditor.jsp" %>


    <script>
        var app_id = "${ddjNews.id}";
        //流程初始化前设置
    </script>
</head>
<body class="haf-form fixed-width with-header with-footer">
<%--流程表单顶部：按钮栏--%>
<div class="haf-form-header"></div>
<%--流程表单背景--%>
<div class="haf-form-backdrop"></div>
<%--流程表单内容--%>
<div class="haf-form-content">
    <%--标题区域--%>
    <div class="page-header <%-- pull-top --%>">
        <h3>编辑新闻信息<small><%--Event Configuration--%></small></h3>
        <ul class="nav nav-tabs">
            <li class="active"><a href="#_hf_apply_info" data-toggle="tab">新闻详情</a></li>
        </ul>
    </div>
    <div class="tab-content">
        <%--申请单信息（用户填写区域）--%>
        <div class="tab-pane active flow-form-info" id="_hf_apply_info">
            <%--基础信息--%>
            <form class="form-horizontal" name="ddjNews" method="post" id="ddjNews">
                <input type="hidden" name="id" value="${ddjNews.id}">

                <div class="form-group">
                    <label class="col-xs-2 control-label">显示内容</label>
                    <div class="col-xs-10">
                        <textarea name="content1" cols="100" rows="8" style="width:750px;height:500px;visibility:hidden;">${detail}</textarea>
                    </div>
                </div>

            </form>
        </div>

    </div>
</div>
<%@ include file="/WEB-INF/haf/form-footer.jsp" %>
<script type="text/javascript" src="${path}/html/ddj/js/form/ddjNews.js"></script>
</body>
</html>