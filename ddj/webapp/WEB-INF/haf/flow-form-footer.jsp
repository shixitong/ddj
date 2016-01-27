<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<div class="flow-form-footer">
    <a href="#" class="show-footer"><span class="glyphicon glyphicon-list"></span></a>
    <div>
        <div class="pull-left">
            <a href="#" class="hide-footer"><span class="glyphicon glyphicon-chevron-down"></span></a>
        </div>
        <span class="pull-right">
            ${sessionScope.SYS_LAST_LOGIN_PERSON.name}
            &nbsp;&nbsp;<span class="timer" style="width:120px;display:inline-block;">2014-01-01 00:00:00</span>
            &nbsp;&nbsp;&nbsp;&nbsp;<a href="#">收藏此页</a>
            &nbsp;&nbsp;|&nbsp;&nbsp;<a href="#">设为主页</a>
        </span>
    </div>
</div>
