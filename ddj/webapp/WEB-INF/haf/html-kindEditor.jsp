<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="stylesheet" href="${path}/html/jquery/kindeditor/themes/default/default.css" />
<link rel="stylesheet" href="${path}/html/jquery/kindeditor/plugins/code/prettify.css" />
<script charset="utf-8" src="${path}/html/jquery/kindeditor/kindeditor.js"></script>
<script charset="utf-8" src="${path}/html/jquery/kindeditor/lang/zh_CN.js"></script>
<script charset="utf-8" src="${path}/html/jquery/kindeditor/plugins/code/prettify.js"></script>
<script>
    var editor1=null;
    KindEditor.ready(function(K) {
     editor1 = K.create('textarea[name="content1"]', {
    cssPath : '${path}/html/jquery/kindeditor/plugins/code/prettify.css',
    uploadJson : '${path}/upload/json/index.do',
    fileManagerJson : '${path}/file/manager/index.do',
    allowFileManager : true,
    afterCreate : function() {
    var self = this;
    K.ctrl(document, 13, function() {
    self.sync();
    document.forms['example'].submit();
    });
    K.ctrl(self.edit.doc, 13, function() {
    self.sync();
    document.forms['example'].submit();
    });
    }
    });
    prettyPrint();
    });
</script>
<![endif]-->
<script>var CONTEXT_PATH = "${path}";</script>
