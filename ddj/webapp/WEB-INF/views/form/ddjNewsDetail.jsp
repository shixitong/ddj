<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/common/include.jsp" %>

<%--表单流程绑定 End--%>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@ include file="/WEB-INF/haf/html-header.jsp" %>
</head>
<body class="haf-form fixed-width with-header with-footer">
<%--流程表单背景--%>
<div class="haf-form-backdrop"></div>
<%--流程表单内容--%>
<div class="haf-form-content">
    <%--标题区域--%>

        <%--申请单信息（用户填写区域）--%>
        <div class="tab-pane active flow-form-info" id="_hf_apply_info">
            <%--基础信息--%>
            <form class="form-horizontal" name="ddjNews" method="post" id="ddjNews">

                <div class="form-group">
                    ${detail}
                </div>

            </form>
        </div>

    </div>
</body>
</html>