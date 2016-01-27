
var url;  //增删改路径
var fromResult=true;//from是否能提交，默认true提交


//加载数据
$(function(){
    $('#menuManage').datagrid({
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
            accountId:$("#menuAccountId").val()
        },
        pageSize:15,
        pageList:[15,30,45],
        url: 'menu/getAll.do',//获取所有菜单
        idField: 'id',
        columns:[[
            {field:'pid',title:'父ID',width:50,align:'center'},
            {field:'accountId',title:'微信账号ID',width:50,align:'center'},
            {field:'name',title:'名称',width:50,align:'center'},
            {field:'type',title:'类型',width:50,align:'center'},
            {field:'url',title:'url[视图按钮]',width:80,align:'center'},
            {field:'key',title:'key[普通按钮]',width:50,align:'center'},
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
            },
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
        toolbar: "#menuToolbar"
    });


    //菜单类型变化
    $('#type').change(function(){
        if( $('#type').val()=='click'){
            $('#menuKey').show();
            $('#menuUrl').hide();
        }else if($('#type').val()=='view'){
            $('#menuUrl').show();
            $('#menuKey').hide();
        }else {
            $('#menuKey').hide();
            $('#menuUrl').hide();
        }
    });

    //按钮KEY变化
    $('#key').change(function(){
        $.ajax({
            type:'post',//可选get
            url:'menu/getByKey.do',
            data:{
              key:$('#key').val(),
              accountId:$('#accountId').val()
            },
            dataType:'text',//服务器返回的数据类型 可选XML ,Json jsonp script html text等
            success:function(result){
            if(result=='true'){
                fromResult=true;
                $("#menuLabel").text("KEY能使用");
                $("#menuLabel").css("background-color","green");
            }else{
               fromResult=false;
                $("#menuLabel").text("KEY不能使用");
                $("#menuLabel").css("background-color","red");
            }
            },
            error:function(msg){
                alert("错误");
            }
        })

    });

    });
//查找数据
function menuFindData(){
    $('#menuManage').datagrid('load',{
            AccountId:$('#menuAccountId').val(),
            createDate:$('#menuCreateDate').datetimebox('getValue'),
            endDate:$('#menuEndDate').datetimebox('getValue'),
            type:$('#menuType').val()
        }
    );
}

//增加窗口
function newMenu(){
    fromResult=true;
    $("#menuLabel").text("");
    $('#menuDlg').dialog('open').dialog('setTitle','新增');
    $('#fm').form('clear');
    //  $('#fm').form('load',{id:''});
    url = 'menu/save.do';
}


//编辑窗口
function editMenu(){
    fromResult=true;
    $("#menuLabel").text("");
    var row = $('#menuManage').datagrid('getSelected');
    var id=row.id;
    if (row){
        $('#menuDlg').dialog('open').dialog('setTitle','编辑');
        $('#pid').combobox({
            url:'menu/menuNameById.do?accountId='+row.accountId,
            textField:"name",
            valueField:"id"
        });
        $('#fm').form('load',row);
        if(row.type=="click"){
            $('#menuKey').show();
            $('#menuUrl').hide();
        }else if(row.type=="view"){
            $('#menuUrl').show();
            $('#menuKey').hide();
        }else{
            $('#menuKey').hide();
            $('#menuUrl').hide();
        }
        url = 'menu/update.do?id='+id;
    }
}

//保存
function saveMenu(){
    if(fromResult){
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
                $('#menuDlg').dialog('close');        // close the dialog
                $('#menuManage').datagrid('reload');    // reload the user data
            }
        }
    });
    }
}

//删除
function destroyMenu(){
    var rows = $('#menuManage').datagrid('getSelected');
    if (rows){
        $.messager.confirm('提示','要删除这条信息吗?',function(r){
            if (r){
                $.post(
                    'menu/delete.do',
                    {id:rows.id},
                    function(result){
                        if (result){
                            $('#menuManage').datagrid('reload');    // reload the user data
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