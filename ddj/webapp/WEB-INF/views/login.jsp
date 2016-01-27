<%@ page language="java" import="java.util.*" pageEncoding="GB18030"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">

    <title>电镀家管理后台</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <style type="text/css">
        body {
            width: 0px;
            height: 0px;
            background-color: #016ba9;
            font-size:12px;
            overflow: hidden;
            text-align: center;
        }
        #father {
            width: 980px;
        }
        #img {
            background-image: url('<%=basePath%>html/img/jia.jpg');
            width: 943px;
            height: 447px;
            margin-left: 160px;
            margin-top: 60px;
        }
        #sr {
            margin-left: 260px;
            margin-top: -240px;

        }
        #yk input{
            width: 120px;
            height: 20px;
            background-color: #27302d;
            margin-bottom: 5px;
            color:#009fc1;
        <!--border:0px;  -->
        }
        #ann input{
            margin-left: 15;
            margin-right: -15;
            border:1px dotted Black;
            background-image: url('<%=basePath%>html/img/dai2.jpg');
            color:  #ffffff;
            cursor: hand;

        }
    </style>
    <script type="text/javascript" >
        function check(){
            var name= document.getElementById("username").value;

            var pwd=document.getElementById("password").value;
            if(name==""||name==null){
                alert("用户名不能为空！");
                return false;
            }
            else if(pwd==""||pwd==null){
                alert("密码不能为空！");
                return false;
            }
        }
    </script>
</head>

<body>
<div id="father">
    <div id="img"></div>
    <div id="sr">
        <form action="${pageContext.request.contextPath }/ddj/ddjAdmin/login.do" method="post">
            <div id="yk">
                用户:<input type="text" name="aname" id="username" value=""/><br/>
                密码:<input type="password" name="apassword" id="password" value=""/><br/>
            </div>
            <div id="ann">
                <input type="submit"   name="登录" value="登录" onclick="return check()">
                <input  type="button" value="注册">
            </div>
        </form>
        <%

            if(request.getAttribute("message")!=null&&request.getAttribute("message").equals("22")){

        %>
        <script type="text/javascript" >
            alert("用户名或密码错误！");
        </script>
        <%
            }
        %>
    </div>
</div>
</body>
</html>
