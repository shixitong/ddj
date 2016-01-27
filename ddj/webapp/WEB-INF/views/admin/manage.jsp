<%--
  Created by IntelliJ IDEA.
  User: wurenhuan
  Date: 14-2-27
  Time: 上午10:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>电镀家管理后台系统</title>
    <base href="<%=basePath%>">
    <link rel="stylesheet" type="text/css" href="<%=basePath %>ej/css/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath %>ej/css/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath %>ej/css/easyui/demo/demo.css">
    <script type="text/javascript" src="<%=basePath %>ej/js/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=basePath %>ej/js/easyui/jquery.easyui.min.js"></script>
    <script type="text/javascript" src="<%=basePath %>ej/js/easyui-lang-zh_CN.js"></script>
    <script>
        $(function(){

        });
        function addTab(title, url){

            if ($('#tt').tabs('exists', title)){

                $('#tt').tabs('select', title);

            } else {

                var content = '<iframe scrolling="auto" frameborder="0"  src="'+url+'"style="width:100%;height:100%;"></iframe>';

                $('#tt').tabs('add',{

                    title:title,

                    content:content,

                    closable:true

                });

            }

        }

        function closet(){
            if (confirm("您确定要退出程序吗？")){
                window.close();
            }
        }
    </script>
</head>
<body class="easyui-layout">
<div region="north" title=" 欢迎进入电镀家后台管理平台！"  split="false" style="height:100px;padding:10px;">
    <p style="float: left;">电镀家管理后台</p>
    <div align="right" id="menu" style="height: 50px;width: 200px;float:right;" >
    <a class="easyui-linkbutton"   iconCls="icon-help" plain="true">帮助</a>
    <a class="easyui-linkbutton"  onclick="closet()"  iconCls="icon-cancel" plain="true">退出</a>
        </div>
</div>
<div region="south" title="版权信息" split="flase" style="height:30px;padding:10px;background:#efefef;">
   <div class="easyui-layout" fit="true" style="background:#ccc;">
       鳄icp编号：v4567467414 电镀家管理后台 copyright：2014-2020
    </div>
</div>
<div region="west" split="true" title="操作菜单" style="width:200px;padding1:1px;overflow:hidden;">
    <div class="easyui-accordion" fit="true" border="false">

        <div title="前台管理" style="padding:10px;overflow:auto;">
            <a href="javascript:addTab('用户信息','ddjUser/index.do')" style="font-weight:bold;">用户信息</a><br/><br/>
            <a href="javascript:addTab('新闻管理','ddj/ddjNews/index.do')"  style="font-weight:bold;">新闻管理</a><br/><br/>
        </div>
        <div title="基础设置" style="padding:10px;overflow:auto;">
            <a href="javascript:addTab('管理员信息','ddj/ddjAdmin/index.do')"  style="font-weight:bold;">管理员信息</a><br/><br/>
        </div>
    </div>
</div>
<div region="center" title="内容" style="overflow:hidden;">
    <div id="tt"class="easyui-tabs" fit="true" border="false">
        <div title="欢迎" style="padding:20px;overflow:hidden;">
            <div style="margin-top:20px;">
                <h2>电镀家管理后台平台说明.</h2>
                <li>1、进入系统后停留时间超过30分钟需重新登录.</li>
                <li>2、加载数据时请勿重复刷新，数据量过大请等待加载完成.</li>
                <li>3、版权所属，侵权必究.</li>
            </div>
        </div>
    </div>
</div>
</body>
</html>