
//加载数据
$(function(){
    $('#messageLogManage').datagrid({
        fit: true,
        fitColumns: true,
        striped: true,
        border: false,
        method: "POST",
        rownumbers: true,
        singleSelect:true,
        nowrap:false,
        pagination: true,
        queryParams:{
            accountId:$("#messageLogAccountId").val()
        },
        pageSize:15,
        pageList:[15,30,45],
        url: 'messageLog/getAll.do',//获取所有菜单
        idField: 'id',
        columns:[[
            {field:'accountId',title:'微信账号ID',width:50,align:'center'},
            {field:'userOpenId',title:'用户',width:50,align:'center'},
            {field:'messageType',title:'消息类型',width:50,align:'center'},
            {field:'mesaageIn',title:'请求信息',width:50,align:'center'},
            {field:'messageOut',title:'回复信息',width:50,align:'center'}
        ]],
        toolbar: "#messageLogToolbar"
    });


});
//查找数据
function messageLogFindData(){
    $('#messageLogManage').datagrid('load',{
            AccountId:$('#messageLogAccountId').val(),
            createDate:$('#messageLogCreateDate').datetimebox('getValue'),
            endDate:$('#messageLogEndDate').datetimebox('getValue')
        }
    );
}

var url;
//增加窗口
function newMessageLog(){
    $('#messageLogDlg').dialog('open').dialog('setTitle','新增');
    $('#fm').form('clear');
    //  $('#fm').form('load',{id:''});
    url = 'messageLog/save.do';
}


//编辑窗口
function editMessageLog(){
    var row = $('#messageLogManage').datagrid('getSelected');
    var id=row.id;
    if (row){
        $('#messageLogDlg').dialog('open').dialog('setTitle','编辑');
        $('#fm').form('load',row);
        url = 'messageLog/update.do?id='+id;
    }
}

//保存
function saveMessageLog(){
    $('#fm').form('submit',{
        url: url,
        onSubmit: function(){
            return $(this).form('validate');
        },
        success: function(result){
            var result = eval('('+result+')');
            if (!result){
                $.messager.show({
                    title: 'Error',
                    msg: '操作有误！'
                });
            } else {
                $('#messageLogDlg').dialog('close');        // close the dialog
                $('#messageLogManage').datagrid('reload');    // reload the user data
            }
        }
    });
}

//删除
function destroyMessageLog(){
    var rows = $('#messageLogManage').datagrid('getSelected');
    if (rows){
        $.messager.confirm('提示','要删除这条信息吗?',function(r){
            if (r){
                $.post(
                    'messageLog/delete.do',
                    {id:rows.id},
                    function(result){
                        if (result){
                            $('#messageLogManage').datagrid('reload');    // reload the user data
                        } else {
                            $.messager.show({    // show error message
                                title: 'Error',
                                msg: '操作有误！'
                            });
                        }
                    },'json');
            }
        });
    }
}
