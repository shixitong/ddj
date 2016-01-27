var resourceGrid,editRow;
$(function(){
    // checkButtons();

    resourceGrid = $('#resourceGrid').treegrid({
        fit:true,
        nowrap: true,
        rownumbers: true,
        border: false,
        url:CONTEXT_PATH+'/system/new/menu/pageList.do',
        idField:'id',
        treeField:'name',
        columns:[[
            {field:'name',title:'菜单名称',width:200},
            {field:'sysId',title:'系统标识',width:60},
            {field:'description',title:'菜单描述',width:200},
            {field:'url',title:'菜单地址',width:200},
            {field:'orderNo',title:'排列序号',width:80},
            {field:'status',title:'状态[1-启用]',width:80},
            {field:'createDate',title:'创建时间',width:130},
            {field:'updateDate',title:'修改时间',width:130}
        ]],
        toolbar: "#resourceGridBar"

    });

    $("#pnodeResource").combotree({
        url:CONTEXT_PATH+'/system/new/menu/getMenuTree.do',
        method:"GET",
        width:250,
        editable:false,
        required:true,
        panelHeight:280,
        //扩展树，使其支技扁平结构的树据
        parentField:"pid",
        textFiled:"text",
        idFiled:"id"
    });



});

/****
 *检查用户是否具有按钮权限
 */
function checkButtons(){
    var uResource = parent.userResources;
    if(uResource != undefined && uResource != null){
        $(".security-button").each(function(){
            if(uResource[$(this).attr("resourceUrl")] == undefined){
                $(this).hide();
            }
        });
    }
}


//添加菜单信息
function addResource(){
//    if($("#menuNode").combobox('getValue') == 0){
//        $("#order").numberspinner('clear');
//        $("#orderDiv").hide();
//    }

    if(typeof(editRow) != 'undefined' ){
        $("#pnodeResource").combotree('setValue',editRow.id);
    }
    var selectRow = $("#resourceGrid").treegrid("getSelected");
    if(selectRow != null){
        $("#pnodeResource").combotree('setValue',selectRow.id);
    }
    if(typeof(editRow) == 'undefined' && selectRow == null ){
        $("#pnodeResource").combotree('setValue','ROOT');
    }
    $('#myDialog').dialog('setTitle',"新增");
    $('#myDialog').dialog("open");
    $('#opereatId').val('add');

}
/***
 *修改菜单信息
 */
function updateResource(){
    var selectRow = $("#resourceGrid").treegrid("getSelected");
    if(typeof(editRow) != 'undefined' ){
        $("#pnodeResource").combotree('setValue',editRow.id);
        selectRow = editRow;
    }
    if(selectRow == null){
        $.messager.alert("提示","请选择一条记录编辑","info");
        return;
    }else{
        $("#pnodeResource").combotree('setValue',selectRow.id);

    }
    $('#myDialog').dialog('open').dialog('setTitle','编辑');
    $("#myForm").form("load",selectRow);
    $('#opereatId').val('update');
//    $("#iconShow").removeClass().addClass($("#icon").combobox("getValue"));
}

/*****
 *删除菜单信息
 */
function deleteResource(){
    var selectRow = $("#resourceGrid").treegrid("getSelected");
    if(selectRow == null){
        $.messager.alert("提示","请选择一条记录删除","info");
        return;
    }
    var deleteRow = selectRow;
    $.messager.confirm("提示","您确定要删除选中行？",function(r){
        if (r){
            $.get(CONTEXT_PATH+"/system/new/menu/deleteMenu.do",{"id":selectRow.id},function(data){
                if(data && data.success){
                    //重新加载treegrid
                    resourceGrid.treegrid('reload');
                }else{
                    $.messager.alert("提示","删除失败","info");
                }
            });
        }
    });
}

/***
 *保存菜单信息
 */
function saveResource(){
    //$("#myForm").submit();
    $.ajax({
        url :CONTEXT_PATH+ "/system/new/menu/saveMenu.do?operate="+$('#opereatId').val(),
        data : $("#myForm").serialize(),
        cache : false,
        dataType : 'json',
        type : 'post',
        beforeSend : function(xhr){
            return $("#myForm").form('validate');
        },
        success : function(result){
            if(result.success){
                closeDialog();
                //重新加载treegrid
                $('#resourceGrid').treegrid('reload');
                $("#pnodeResource").combotree("reload");
            }else{
                $.messager.alert("错误",result.msg,"error");
            }
        }
    });
}
function closeDialog(){
    $("#myDialog").dialog("close");
}
function resetForm(){
    $('#myForm').form('clear');
    $('#myForm')[0].reset();
    $("#order").numberspinner('setValue',10);
    $('div.validatebox-tip').remove();
}

/**
 * Created with IntelliJ IDEA.
 * User: JinShaowei
 * Date: 12-10-31
 * Time: 下午3:16
 * Description:
 * 扁平结构的数据加载器
 */
$.fn.tree.defaults.loadFilter = function (data, parent) {
    var opt = $(this).data().tree.options;
    var idFiled,
        textFiled,
        parentField;
    if (opt.parentField) {
        idFiled = opt.idFiled || 'id';
        textFiled = opt.textFiled || 'text';
        parentField = opt.parentField;

        var i,
            l,
            treeData = [],
            tmpMap = [];

        for (i = 0, l = data.length; i < l; i++) {
            tmpMap[data[i][idFiled]] = data[i];
        }

        for (i = 0, l = data.length; i < l; i++) {
            if (tmpMap[data[i][parentField]] && data[i][idFiled] != data[i][parentField]) {
                if (!tmpMap[data[i][parentField]]['children'])
                    tmpMap[data[i][parentField]]['children'] = [];
                data[i]['text'] = data[i][textFiled];
                tmpMap[data[i][parentField]]['children'].push(data[i]);
            } else {
                data[i]['text'] = data[i][textFiled];
                treeData.push(data[i]);
            }
        }
        return treeData;
    }
    return data;
};


/****
 * 让combotree也支持扁平数据格式的数据
 * @param data
 * @param parent
 * @return {*}
 */
$.fn.combotree.defaults.loadFilter = function (data, parent) {
    var opt = $(this).data().tree.options;
    var idFiled,
        textFiled,
        parentField;
    if (opt.parentField) {
        idFiled = opt.idFiled || 'id';
        textFiled = opt.textFiled || 'text';
        parentField = opt.parentField;

        var i,
            l,
            treeData = [],
            tmpMap = [];

        for (i = 0, l = data.length; i < l; i++) {
            tmpMap[data[i][idFiled]] = data[i];
        }

        for (i = 0, l = data.length; i < l; i++) {
            if (tmpMap[data[i][parentField]] && data[i][idFiled] != data[i][parentField]) {
                if (!tmpMap[data[i][parentField]]['children'])
                    tmpMap[data[i][parentField]]['children'] = [];
                data[i]['text'] = data[i][textFiled];
                tmpMap[data[i][parentField]]['children'].push(data[i]);
            } else {
                data[i]['text'] = data[i][textFiled];
                treeData.push(data[i]);
            }
        }
        return treeData;
    }
    return data;
};