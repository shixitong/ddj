


//加载数据
$(function(){
    $('#accountManage').datagrid({
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
            accountId:$("#accountAccountId").val()
        },
        pageSize:15,
        pageList:[15,30,45],
        url: 'account/getAll.do',//获取所有菜单
        idField: 'id',
        columns:[[
            {field:'accountId',title:'微信账号ID',width:50,align:'center'},
            {field:'token',title:'校验token',width:50,align:'center'},
            {field:'appId',title:'appId',width:50,align:'center'},
            {field:'appSecret',title:'appSecret',width:50,align:'center'},
            {field:'accountName',title:'名称',width:50,align:'center'},
            {field:'description',title:'描述',width:50,align:'center'},
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
        toolbar: "#accountToolbar"
    });

});
//查找数据
function accountFindData(){
    $('#accountManage').datagrid('load',{
            AccountId:$('#accountAccountId').val(),
            accountName:$('#ACCOUNT_NAME').val()
          //  endDate:$('#accountEndDate').datetimebox('getValue')
        }
    );
}

var url;
//增加窗口
function newAccount(){
    $('#accountDlg').dialog('open').dialog('setTitle','新增');
    $('#fm').form('clear');
    //  $('#fm').form('load',{id:''});
    url = 'account/save.do';
}


//编辑窗口
function editAccount(){
    var row = $('#accountManage').datagrid('getSelected');
    var id=row.id;
    if (row){
        $('#accountDlg').dialog('open').dialog('setTitle','编辑');
        $('#fm').form('load',row);
        url = 'account/update.do?id='+id;
    }
}

//保存
function saveAccount(){
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
                $('#accountDlg').dialog('close');        // close the dialog
                $('#accountManage').datagrid('reload');    // reload the user data
            }
        }
    });
}

//删除
function destroyAccount(){
    var rows = $('#accountManage').datagrid('getSelected');

    if (rows){
        $.messager.confirm('提示','要删除这条信息吗?',function(r){
            if (r){
                $.post(
                    'account/delete.do',
                    {id:rows.id},
                    function(result){
                        if (result){
                            $('#accountManage').datagrid('reload');    // reload the user data
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