// 扩展array包含某个元素的方法
Array.prototype.contains = function(obj) {
    var i = this.length;
    while (i--) {
        if (this[i] === obj) {
            return true;
        }
    }
    return false;
}

var table;
var accessTable;
var myAccessTable;
var isFirst = false;

$(function(){

    //用户dataGrid
    table = $('#dataList').treegrid({
        fit:true,
        nowrap: true,
        fitColumns:true,
        rownumbers: true,
        border: false,
        url: '/system/role/pageList.do',
        idField: 'id',
        treeField:'roleDesc',
        columns: [[
            {field:'value',title:'角色描述',width:150,align:'left'},
            {field:'text',title:'角色名',width:150,align:'left'},
            {field:'description',title:'描述',width:120,align:'center'},
            {field:'resourceAction',title:'权限操作',width:230,align:'center',formatter:function(value,row,index){
                if(!(row.pnode == "ROOT")){
                    return "<a style='color:red;' href='javascript:showMenuTree(\""+ row.value +"\")'>角色菜单管理</a>";
               /*     "&nbsp;&nbsp;&nbsp;<a style='color:red;' href= '/haf/system/role/setRoleAndUser.do?id=" + row.id + "' target='_blank'>角色人员管理</a>"+
                    "&nbsp;&nbsp;&nbsp;<a style='color:red;' " +
                        "href='javascript:showResourceTree(\""+ row.id +"\")'>角色权限管理</a>"+
                        "&nbsp;&nbsp;&nbsp;<a style='color:red;' " +
                        "href='javascript:showAccess(\""+index +"\")'>ACL管理</a>";*/
                }
            }}
        ]]/*,
        toolbar: "#dataListBar"*/
    });

/*    //角色列表
    accessTable = $('#accessDataList').datagrid({
        fit: true,
        fitColumns: true,
        striped: true,
        border: false,
        method: "POST",
        pagination: true,
        pageSize:10,
        pageList:[10,30,60,120,450],
        url: '/system/access/pageList.do',
        idField: 'id',
        columns: [[
            {field:'ck',checkbox:true},
            {field:'accessName',title:'角色描述',width:200,align:'center'}
        ]],
        onLoadSuccess:function(){
            //判断是否已经加载过
            if(isFirst){
                //获取当前自己所有的权限
                var roles = myAccessTable.datagrid("getRows");
                //在权限列表选中自己拥有的权限
                $.each(roles,function(index,data){
                    accessTable.datagrid("selectRecord",data.id);
                });
            }
        },
        onSelect:function(index,data){
            //添加自己拥有的权限
            if(isFirst){
                if(myAccessTable.datagrid("getRowIndex",data.id) == -1){
                    myAccessTable.datagrid("appendRow",data);
                }
            }
        },
        onUnselect:function(index,data){
            //获取自己拥有权限的下标
            var row = myAccessTable.datagrid("getRowIndex",data.id);
            //删除自己拥有的权限
            myAccessTable.datagrid("deleteRow",row);
        },
        onSelectAll:function(rows){
            $.each(rows,function(index,data){
                if(myAccessTable.datagrid("getRowIndex",data.id) == -1){
                    myAccessTable.datagrid("appendRow",data);
                }
            });
        },
        onUnselectAll: function(rows){
            $.each(rows,function(index,data){
                //获取自己拥有权限的下标
                var row = myAccessTable.datagrid("getRowIndex",data.id);
                if(myAccessTable.datagrid("getRowIndex",data.id) != -1){
                    //删除自己拥有的权限
                    myAccessTable.datagrid("deleteRow",row);
                }
            });
        }
    });

    $.ajax({
        type:'post',
        url:'/system/role/getRootRole.do',
        success:function(result){
            $.each(result,function(index,data){
                $("#myForm select[name=pnode]").append("<option value='"+ data.id +"'>" + data.roleDesc + "</option>")
            })
        }
    });*/
});



function showAccess(index){
    var id = table.treegrid("getSelected").id;
    myAccessTable = $('#myAccessDataList').datagrid({
        fit: true,
        fitColumns: true,
        striped: true,
        border: false,
        method: "POST",
        url: '/system/role/findRoleAccess.do?id=' + id,
        idField: 'id',
        columns: [[
            {field:'accessName',title:'我的角色',width:200,align:'center'}
        ]],
        onLoadSuccess:function(){
            var roles = myAccessTable.datagrid("getRows");
            $.each(roles,function(index,data){
                accessTable.datagrid("selectRecord",data.id);
            });
            isFirst = true;
        }
    });

    $("#roleId").val(id);

    $('#accessDialog').dialog('setTitle',"查看所属角色");
    $('#accessDialog').dialog("open");
}

function saveRoleAccess(){
    var roleAccessList = new Object();
    var accessList = new Array();
    $.each(myAccessTable.datagrid("getRows"),function(index,data){
        var access = new Object();
        access.id = data.id;
        accessList.push(access);
    })
    roleAccessList.accessList = accessList;
    roleAccessList.roleId = $("#roleId").val();

    $.ajax({
        type:'POST',
        url: '/system/role/saveRoleAccess.do',
        headers:{'Content-Type':'application/json;charset=UTF-8'},
        mimetype :"application/json",
        data: JSON.stringify(roleAccessList),
        success: function(){
            $.messager.alert("提示","保存成功","info");
        },
        error:function(){
            $.messager.alert("错误","保存失败","error");
        }
    })
}



/***
 * 显示角色拥有的资源权限树
 */
var roleIdAll;
var roleResourceAll = [];
var delRoleResource = new Object();
var addRoleResource = new Object();



var roleMenuAll = [];
var delRoleMenu = new Object();
var addRoleMenu = new Object();


function showResourceTree(roleId){
    //初始化资源树数据
    roleIdAll = roleId;
    delRoleResource = new Object();
    addRoleResource = new Object();
    roleResourceAll = [];
    $("#dataList").treegrid("clearSelections");

    $("#resourcesTree").tree({
        url:'/system/resource/getResourceTree.do?id=0',
        method:"GET",
        checkbox:true,
        cascadeCheck:false,//取消级联选中
        //扩展树，使其支技扁平结构的树据
        parentField:"pid",
        textFiled:"text",
        idFiled:"id",
        onBeforeExpand: function(node){
            if(node){
                $('#resourcesTree').tree('options').url = "/system/resource/getResourceTree.do";
            }
        },
        onLoadSuccess:function(){
            $.get("/system/role/getResourceIdsByRoleId.do",{"roleId":roleId},function(data){
                if(data){
                    //遍历树，根据查询到的节点，选中已有的权限节点
                    $(data.nodes).each(function(index,val){
                        var node = $('#resourcesTree').tree('find', val);
                        if(node != null){
                            $('#resourcesTree').tree('check', node.target);
                            roleResourceAll[node.attributes.dataId] = true;
                        }
                    });
                }
            });
        },
        onCheck:function(node,checked){
            if(checked){
                addRoleResource[node.attributes.dataId] = true;
                delRoleResource[node.attributes.dataId] = false;
            }else{
                addRoleResource[node.attributes.dataId] = false;
                delRoleResource[node.attributes.dataId] = true;
            }
        },
        onDblClick: function(node){
            $('#resourcesTree').tree('toggle',node.target);
        }
    });

    $("#resourcesDialog").dialog("open");
}


/**
 * 加载菜单树
 */
function showMenuTree(roleId){
    //初始化资源树数据
    roleIdAll = roleId;
    delRoleMenu = new Object();
    addRoleMenu = new Object();
    roleResourceAll = [];
    $("#dataList").treegrid("clearSelections");

    $("#menuTree").tree({
        url:'/system/new/menu/getMenuTree.do',
        method:"GET",
        checkbox:true,
        cascadeCheck:false,//取消级联选中
        //扩展树，使其支技扁平结构的树据
        parentField:"pid",
        textFiled:"text",
        idFiled:"id",
        onBeforeExpand: function(node){
            if(node){
                $('#menuTree').tree('options').url = "/system/new/menu/getMenuTree.do";
            }
        },
        onLoadSuccess:function(){
            $.get("/system/role/getMenuIdsByRoleId.do",{"roleId":roleId},function(data){
                if(data){
                    //遍历树，根据查询到的节点，选中已有的权限节点
                    $(data.nodes).each(function(index,val){
                        var node = $('#menuTree').tree('find', val);
                        if(node != null){
                            $('#menuTree').tree('check', node.target);
                            roleMenuAll[node.attributes.dataId] = true;
                        }
                    });
                }
            });
        },
        onCheck:function(node,checked){
            if(checked){
                addRoleMenu[node.attributes.dataId] = true;
                delRoleMenu[node.attributes.dataId] = false;
            }else{
                addRoleMenu[node.attributes.dataId] = false;
                delRoleMenu[node.attributes.dataId] = true;
            }
        },
        onDblClick: function(node){
            $('#menuTree').tree('toggle',node.target);
        }
    });

    $("#menuDialog").dialog("open");
}

/**
 * 保存角色菜单
 *
 */
function saveRoleMenu(){
    var removeRoleMenu = new Array();
    var saveRoleMenu = new Array();

    $.each(delRoleMenu,function(inx,data){
        if(delRoleMenu[inx]){
            if(roleMenuAll[inx] != undefined){
                removeRoleMenu.push(inx);
            }
        }
    })

    $.each(addRoleMenu,function(inx,data){
        if(addRoleMenu[inx]){
            if(roleMenuAll[inx] == undefined){
                saveRoleMenu.push(inx);
            }
        }
    })

    var menuListVo = new Object();
    menuListVo.removeRoleMenu = removeRoleMenu;
    menuListVo.saveRoleMenu =saveRoleMenu;
    menuListVo.roleId = roleIdAll;

    $.ajax({
        type:'POST',
        url: '/system/role/saveRoleMenu.do',
        headers:{'Content-Type':'application/json;charset=UTF-8'},
        mimetype :"application/json",
        data: JSON.stringify(menuListVo),
        success: function(data){
            $.messager.alert("提示","保存成功","info");
            $("#resourcesDialog").dialog("close");
        },
        error:function(){
            $.messager.alert("错误","保存失败","error");
        }
    })
}



function saveRoleTest(){
    var removeRoleResource = new Array();
    var saveRoleResource = new Array();

    $.each(delRoleResource,function(inx,data){
        if(delRoleResource[inx]){
            if(roleResourceAll[inx] != undefined){
                removeRoleResource.push(inx);
            }
        }
    })

    $.each(addRoleResource,function(inx,data){
        if(addRoleResource[inx]){
            if(roleResourceAll[inx] == undefined){
                saveRoleResource.push(inx);
            }
        }
    })

    var resourceListVo = new Object();
    resourceListVo.removeRoleResource = removeRoleResource;
    resourceListVo.saveRoleResource =saveRoleResource;
    resourceListVo.roleId = roleIdAll;

    $.ajax({
        type:'POST',
        url: '/system/role/saveRoleResource.do',
        headers:{'Content-Type':'application/json;charset=UTF-8'},
        mimetype :"application/json",
        data: JSON.stringify(resourceListVo),
        success: function(data){
            $.messager.alert("提示","保存成功","info");
            $("#resourcesDialog").dialog("close");
        },
        error:function(){
            $.messager.alert("错误","保存失败","error");
        }
    })
}

//添加角色信息
function addRole(){
    $('#myDialog').dialog('setTitle',"新增");
    $('#myDialog').dialog("open");
}
/***
 *修改角色信息
 */
function updateRole(){
    var selectRows = $("#dataList").treegrid("getSelected");
    if(selectRows == null){
        $.messager.alert("提示","请选择一条记录编辑","info");
        return;
    }

    $('#myDialog').dialog('open').dialog('setTitle','编辑');
    $("#myForm").form("load",selectRows);
}


/*****
 *删除角色信息
 */
function deleteRole(){
    var selectRows = $("#dataList").treegrid("getSelected");
    if(selectRows == null){
        $.messager.alert("提示","请选择一条记录删除","info");
        return;
    }
    $.messager.confirm("提示","您确定要删除选中行？",function(r){
        if (r){
            $.get("/system/role/deleteRole.do",{"id":selectRows.id},function(data){
                if(data && data.success){
                    //重新加载角色列表
                    table.treegrid('reload');
                }else{
                    $.messager.alert("提示","删除失败","info");
                }
            });
        }
    });
}

/***
 *保存角色信息
 */
function saveRole(){
    //表单验证
    if(!$("#myForm").form('validate')){
        return;
    }
    //如果表单验证通过，则进行角色唯一性验证
    //当前输入的角色名
    var valid = true;
    var roleName = $("#myForm input[name='roleName']").val();
    //当前表单的ID
    var id = $("#myForm input[name='id']").val();
    $.ajax({type: "POST",url: "/system/role/checkRoleNameExist.do",data: "roleName="+roleName,async:false,
        success: function(result){
            if(result.status == 2){
                //判断是添加还是修改，如果是修改，则要比较从后台返回的记录ID，如果记录ID相同，则可以修改
                if(null == id || "" == id){ //添加操作,但是角色名已被使用，所以阻止掉
                    valid = false;
                    $.messager.alert("提示","角色名已被使用","info");
                }else{ //修改
                    if(id != result.msg){//记录ID不相同，则说明修改后的角色名已被使用
                        valid = false;
                        $.messager.alert("提示","角色名已被使用,修改失败!","info");
                    }
                }
            }
            if(result.status == 3){ //后台出现异常
                valid = false;
                $.messager.alert("错误",result.msg,"error");
            }
        }
    });
    if(!valid){
        return;
    }
    //组装对象
    var role = new Object();
    role.id = $("#myForm input[name='id']").val();
    role.roleName = $("#myForm input[name='roleName']").val();
    role.roleDesc = $("#myForm input[name='roleDesc']").val();
    role.status = $("#status").combobox("getValue");
    role.pnode = $("#myForm select[name='pnode']").val();

    //ajax异步提交保存,  headers，minetype属性是后台springmvc 注解@RequestBody解析实体类需要的参数
    $.ajax({
        type: "POST",
        url: "/system/role/save.do",
        headers:{'Content-Type':'application/json;charset=UTF-8'},
        mimetype :"application/json",
        data: JSON.stringify(role),
        success: function(result){
            if(result.success){
                $.messager.alert("提示","保存成功","info");
                closeDialog();
                //重新加载表格
                table.treegrid('reload');
            }else{
                $.messager.alert("错误","保存信息失败","error");
            }
        },
        error:function(){
            $.messager.alert("错误","保存信息出错","error");
        }
    });
}
//关闭表单窗口
function closeDialog(){
    $("#myDialog").dialog("close");
}
//重置表单
function resetForm(){
    $('#myForm').form('clear');
    $('#myForm')[0].reset();
    $('div.validatebox-tip').remove();
    $(".easyui-validatebox").removeClass("validatebox-invalid");
}

//获取选中的节点（资源）的ID
function getSelectResourceId(){
    var resourceTree = $("#resources").combotree("tree");
    var nodes = resourceTree.tree("getChecked");
    var resourceIdArray = new Array();
    $(nodes).each(function(index,val){
        // 获取权限的ID,并不是树节点的ID
        resourceIdArray.push(val.attributes.dataId);
    });
    return resourceIdArray;
}

function reloadRole(){
    $.messager.confirm("提示","系统将重新加载您修改过的角色信息，如果还需要修改，请点击取消按钮继续修改。",function(r){
        if(!r){
            return;
        }else{
            $.get("/system/role/reloadRoleResource.do",function(data){
                if(!data.success){
                    $.messager.alert("错误",data.msg,"error");
                }else{
                    parent.userResources = new Object();
                    //获取当前用户拥有的资源
                    $.get("/system/loadResource.do",function(data){
                        if(data){
                            $.each(data,function(index,val){
                                parent.userResources[val.url] = index+"";
                            });
                        }
                    });
                    $.messager.alert("提示",data.msg,"info");
                }
            });
        }
    });
}



function  closeRoleAccess(){
    accessTable.datagrid("unselectAll");
    $("#dataList").treegrid("clearSelections");
}