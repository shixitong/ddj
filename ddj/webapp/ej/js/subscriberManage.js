
//加载数据
$(function(){

    $('#subscriberManage').datagrid({
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
            accountId:$("#subscriberAccountId").val()
        },
        pageSize:15,
        pageList:[15,30,45],
        url: 'subscriber/getAll.do',//获取所有菜单
        idField: 'id',
        columns:[[
            {field:'accountId',title:'微信账号ID',width:50,align:'center'},
            {field:'groupId',title:'群组ID',width:50,align:'center'},
            {field:'subscribe',title:'关注状态',width:50,align:'center'},
            {field:'openId',title:'用户标示',width:50,align:'center'},
            {field:'nikeName',title:'昵称',width:50,align:'center'},
            {field:'sex',title:'性别',width:50,align:'center'},
            {field:'country',title:'国家',width:50,align:'center'},
            {field:'province',title:'省份',width:50,align:'center'},
            {field:'city',title:'城市',width:50,align:'center'},
            {field:'language',title:'语言',width:50,align:'center'},
            {field:'headImageUrl',title:'头像链接',width:50,align:'center'},
            {field:'subscribeTime',title:'最后关注时间',width:100,align:'center'},
            {field:'createDate',title:'创建时间',width:100,align:'center'},
            {field:'updateDate',title:'修改时间',width:100,align:'center'},
            {field:'status',title:'状态',width:80,align:'center',
                formatter: function(value,row,index){
                    if (row.status==0){
                        return "未使用";
                    } else if(row.status==1) {
                        return "使用";
                    }else{
                        return "获取状态失败";
                    }
                }
            }
        ]],
        toolbar: "#subscriberToolbar"
    });

});
//查找数据
function subscriberFindData(){
    $('#subscriberManage').datagrid('load',{
            AccountId:$('#subscriberAccountId').val(),
            createDate:$('#subscriberCreateDate').datetimebox('getValue'),
            endDate:$('#subscriberEndDate').datetimebox('getValue')
        }
    );
}

var url;
//增加窗口
function newSubscriber(){
    $('#subscriberDlg').dialog('open').dialog('setTitle','新增');
    $('#fm').form('clear');
    //  $('#fm').form('load',{id:''});
    url = 'subscriber/save.do';
}


//编辑窗口
function editSubscriber(){
    var row = $('#subscriberManage').datagrid('getSelected');
    var id=row.id;
    if (row){
        $('#subscriberDlg').dialog('open').dialog('setTitle','编辑');
        $('#groupId').combobox('reload','group/groupNamebyId.do?accountId='+row.accountId);
        $('#fm').form('load',row);
        url = 'subscriber/update.do?id='+id;
    }
}

//保存
function saveSubscriber(){
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
                $('#subscriberDlg').dialog('close');        // close the dialog
                $('#subscriberManage').datagrid('reload');    // reload the user data
            }
        }
    });
}

//删除
function destroySubscriber(){
    var rows = $('#subscriberManage').datagrid('getSelected');
    if (rows){
        $.messager.confirm('提示','要删除这条信息吗?',function(r){
            if (r){
                $.post(
                    'subscriber/delete.do',
                    {id:rows.id},
                    function(result){
                        if (result){
                            $('#subscriberManage').datagrid('reload');    // reload the user data
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
