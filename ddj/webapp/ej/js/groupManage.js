
//加载数据
$(function(){
    $('#groupManage').datagrid({
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
            accountId:$("#accountId").val(),
           endDate:$('#endDate0').datebox('getValue')
        },
        pageSize:15,
        pageList:[15,30,45],
        url: 'group/getAll.do',//获取所有菜单
        idField: 'id',
        columns:[[
            //hidden:true  此属性隐藏列
            {field:'accountId',title:'微信账号ID',width:50,align:'center'},
            {field:'groupId',title:'群组ID',width:50,align:'center'},
            {field:'groupName',title:'群组名称',width:50,align:'center'},
            {field:'createDate',title:'创建时间',width:100,align:'center'},
            {field:'updateDate',title:'修改时间',width:100,align:'center'},
            {field:'delFlag',title:'是否删除',width:80,align:'center',
                formatter: function(value,row,index){
                    if (row.delFlag==0){
                        return "未删除";
                    } else if(row.delFlag==1) {
                        return "删除";
                    }else{
                        return "获取状态失败";
                    }
                }
            }
        ]],
        toolbar: "#groupToolb"
    });

});
//查找数据
function FindData0(){
    $('#groupManage').datagrid('load',{
            accountId:$('#accountId').val(),
            createDate:$('#createDate0').datetimebox('getValue'),
            endDate:$('#endDate0').datetimebox('getValue')
        }
    );
}

var url;
var row;
//增加窗口
function newGroup(){
    $('#dlg').dialog('open').dialog('setTitle','新增');
    $('#fm').form('clear');
    url = 'group/save.do';
}


//编辑窗口
function editGroup(){
     row = $('#groupManage').datagrid('getSelected');
    var id=row.id;
    if (row){
        $('#dlg').dialog('open').dialog('setTitle','编辑');
        $('#fm').form('load',row);
        url = 'group/update.do?id='+id;
    }
}

//保存
function saveGroup(){
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
                $('#dlg').dialog('close');        // close the dialog
                $('#groupManage').datagrid('reload');    // reload the user data
            }
        }
    });
}

//删除
function destroyGroup(){
    var rows = $('#groupManage').datagrid('getSelected');
    if (rows){
        $.messager.confirm('提示','要删除这条信息吗?',function(r){
            if (r){
                $.post(
                    'group/delete.do',
                    {id:rows.id},
                    function(result){
                        if (result){
                            $('#groupManage').datagrid('reload');    // reload the user data
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