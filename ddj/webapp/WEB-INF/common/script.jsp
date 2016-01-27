<script type="text/javascript" src="${path }/html/system/js/json2.js?ver=${scriptVersion}"></script>
<script type="text/javascript" src="${path }/html/system/js/commons.js?ver=${scriptVersion}"></script>
<script type="text/javascript" src="${path }/html/system/js/app.js?ver=${scriptVersion}"></script>
<script type="text/javascript" src="${path}/html/system/js/extend.js?ver=${scriptVersion}"></script>
<!--[if lt IE 9]>
<script type="text/javascript" src="${path}/html/easyui/html5.js?ver=${scriptVersion}"></script>
<![endif]-->
<%--<script type="text/javascript" src="${path}/html/easyui/jquery.placeholder.min.js?ver=${scriptVersion}"></script>--%>

<script type="text/javascript">
    $(function(){
        $.ajaxSetup({contentType: 'application/x-www-form-urlencoded; charset=UTF-8'});
        $('a[href="#"]').attr('href','javascript:void(0)');
        /*$('input[placeholder], textarea[placeholder]').placeholder();*/
    });
</script>