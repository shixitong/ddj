
//加载数据
$(function(){
    $('#newsManage').datagrid({
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
            accountId:$("#newsAccountId").val()
        },
        pageSize:15,
        pageList:[15,30,45],
        url: 'news/getAll.do',//获取所有菜单
        idField: 'id',
        columns:[[
            {field:'accountId',title:'微信账号ID',width:50,align:'center'},
            {field:'title',title:'标题',width:50,align:'center'},
            {field:'description',title:'描述',width:50,align:'center'},
            {field:'url',title:'跳转链接',width:50,align:'center'},
            {field:'picUrl',title:'图片链接',width:50,align:'center'},
            {field:'publishDate',title:'发布时间',width:100,align:'center'},
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
        toolbar: "#newsToolbar"
    });

});
//查找数据
function newsFindData(){
    $('#newsManage').datagrid('load',{
            AccountId:$('#newsAccountId').val(),
            title:$('#newsTitle').val(),
            createDate:$('#newsCreateDate').datetimebox('getValue'),
            endDate:$('#newsEndDate').datetimebox('getValue')
        }
    );
}

var url;
//增加窗口
function newNews(){
    $('#newsDlg').dialog('open').dialog('setTitle','新增');
    $('#fm').form('clear');
    //  $('#fm').form('load',{id:''});
    url = 'news/save.do';
}


//编辑窗口
function editNews(){
    var row = $('#newsManage').datagrid('getSelected');
    var id=row.id;
    if (row){
        $('#newsDlg').dialog('open').dialog('setTitle','编辑');
        $('#fm').form('load',row);
        url = 'news/update.do?id='+id;
    }
}

//保存
function saveNews(){
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
                $('#newsDlg').dialog('close');        // close the dialog
                $('#newsManage').datagrid('reload');    // reload the user data
            }
        }
    });
}

//删除
function destroyNews(){
    var rows = $('#newsManage').datagrid('getSelected');
    if (rows){
        $.messager.confirm('提示','要删除这条信息吗?',function(r){
            if (r){
                $.post(
                    'news/delete.do',
                    {id:rows.id},
                    function(result){
                        if (result){
                            $('#newsManage').datagrid('reload');    // reload the user data
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
