<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<c:set var="HAF_FLOW_TASK_USER_ID" value="${param._hf_task_user_id}" />
<c:set var="HAF_FLOW_INST_ID" value="${param._hf_inst_id}" />
<c:set var="HAF_FLOW_DATA_ID" value="${param._hf_data_id}" />
<c:set var="HAF_FLOW_EXEC_ID" value="${param._hf_execution_id}" />

<link rel="stylesheet" type="text/css" href="${resPath}/easyui/themes/bootstrap/easyui.haf.min.css?ver=${scriptVersion}">
<link rel="stylesheet" type="text/css" href="${resPath}/easyui/themes/icon.css?ver=${scriptVersion}">
<script type="text/javascript" src="${resPath}/easyui/easyui.haf.min.js?ver=${scriptVersion}"></script>

<link rel="stylesheet" type="text/css" href="${resPath}/haf/css/haf.flow.min.css?ver=${scriptVersion}">
<script type="text/javascript" src="${resPath}/haf/js/haf.flow.min.js?ver=${scriptVersion}"></script>

<script>
    //设置HAF基础路径（根路径）
    HAF.basePath("http://local.hxoadev.com:8083/haf/");
    //流程基础参数定义（流程初始化前）
    HAF.Flow.set({
        flowKeyId: "${HAF_FLOW_KEY_ID}",
        taskUserId: "${HAF_FLOW_TASK_USER_ID}",
        instId: "${HAF_FLOW_INST_ID}",
        dataId: "${HAF_FLOW_DATA_ID}",
        formId: "${HAF_FLOW_FORM_ID}",
        executionId: "${HAF_FLOW_EXEC_ID}"
    });
</script>
