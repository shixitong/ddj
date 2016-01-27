<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<c:set var="HAF_FLOW_TASK_USER_ID" value="${param._hf_task_user_id}" />
<c:set var="HAF_FLOW_DATA_ID" value="${param._hf_data_id}" />
<c:set var="HAF_FLOW_EXEC_ID" value="${param._hf_execution_id}" />

<%--<link rel="stylesheet" type="text/css" href="${resPath}/bootstrap/css/bootstrap-datepicker.css?ver=${scriptVersion}">
<script type="text/javascript" src="${resPath}/bootstrap/js/bootstrap-datepicker.js?ver=${scriptVersion}"></script>--%>

<link rel="stylesheet" type="text/css" href="${resPath}/moblie/haf/css/haf.all.min.css?ver=${scriptVersion}">
<%--<link rel="stylesheet" type="text/css" href="${path}/html/moblie/haf/css/haf.mask.css?ver=${scriptVersion}">
<link rel="stylesheet" type="text/css" href="${path}/html/moblie/haf/css/haf.dialog.css?ver=${scriptVersion}">
<link rel="stylesheet" type="text/css" href="${path}/html/moblie/haf/css/haf.form.css?ver=${scriptVersion}">
<link rel="stylesheet" type="text/css" href="${path}/html/moblie/haf/css/haf.flow.css?ver=${scriptVersion}">--%>

<script type="text/javascript" src="${resPath}/moblie/haf/js/haf.all.min.js?ver=${scriptVersion}"></script>
<%--<script type="text/javascript" src="${path}/html/moblie/haf/js/haf.js?ver=${scriptVersion}"></script>
<script type="text/javascript" src="${path}/html/moblie/haf/js/haf.mask.js?ver=${scriptVersion}"></script>
<script type="text/javascript" src="${path}/html/moblie/haf/js/haf.dialog.js?ver=${scriptVersion}"></script>
<script type="text/javascript" src="${path}/html/moblie/haf/js/haf.form.js?ver=${scriptVersion}"></script>
<script type="text/javascript" src="${path}/html/moblie/haf/js/haf.flow.js?ver=${scriptVersion}"></script>--%>
<script>
    var resPath='http://hafdev.hxoadev.com/public/';
    //设置HAF基础路径（根路径）
    HAF.basePath("${path}");
    //流程基础参数定义（流程初始化前）
    HAF.Flow.set({
        flowKeyId: "${HAF_FLOW_KEY_ID}",
        taskUserId: "${HAF_FLOW_TASK_USER_ID}",
        dataId: "${HAF_FLOW_DATA_ID}",
        formId: "${HAF_FLOW_FORM_ID}",
        executionId: "${HAF_FLOW_EXEC_ID}"
    });
    //
    $(document).delegate("a","focus",function(){this.blur();});
</script>
