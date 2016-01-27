//初始化信息
var $ddjAdminGrid;
$(function (){
    var gridOptions = {
        width: "100%",
        height:"540px",
        singleSelect:true,
        pagination:true,
        rownumbers:true,
        fitColumns:true,
        border:true,
        url: HAF.basePath()+"/ddj/ddjAdmin/list/getAllList.do",
        //queryParams: {appId: id},
        searchParams: [
            {name:"aname",label: " 管理员登陆名",type:"text"}
        ],
        columns: [
            {field:'id',checkbox:true},
            {field:'aname',title:'管理员登陆名',width:90,align:'center'},
            {field:'apassword',title:'密码',width:90,align:'center'},
            {field:'createdate',title:'创建时间',width:90,align:'center'}
        ],
        toolbar: ['-',{
            text: "新增",
            iconCls: 'icon-add',
            handler: function(){
                editDdjAdmin("添加", function(data){
                    saveDdjAdmin(data, function(data){
                        $ddjAdminGrid.datagrid("reload");
                    });
                });
            }
        }, '-',{
            text: "删除",
            iconCls: 'icon-remove',
            handler: function(){
            if (window.confirm("是否执行此操作?")) {
                var rows = $ddjAdminGrid.datagrid("getChecked");
                if(rows.length==0){
                    alert("请选择需要删除的行");
                    return false;
                }
                var ids = [];
                $.each(rows,function(i,row){
                    if(row.id) ids.push(row.id);
                    var index = $ddjAdminGrid.datagrid("getRowIndex",row);
                    $ddjAdminGrid.datagrid("deleteRow",index).datagrid("acceptChanges");
                });
                //
                if(ids.length){
                    deleteDdjAdmin(ids);
                }
            }

            }
        }],
        onDblClickRow: function(rowIndex, rowData){
            editDdjAdmin("信息", function(data){
                saveDdjAdmin(data, function(data){
                    $ddjAdminGrid.datagrid("updateRow",{index:rowIndex,row:data})
                        .datagrid("acceptChanges");
                });
            }, rowData);
        }
    };


    $ddjAdminGrid = HAF.Form.searchGrid("#ddjAdminGrid",gridOptions);
});


//编辑信息
function editDdjAdmin(title, callback, row){

    HAF.Form.gridEditor({
        title: title,
        visible: [
            [
                {label: "管理员登陆名",labelWidth:"2",name:"aname",fieldWidth:"4",type:"text", readonly: false,options:{}},
                {label: "密码",labelWidth:"2",name:"apassword",fieldWidth:"4",type:"text", readonly: false,options:{}}
            ]
          ],
        rowData: row,
        callback: callback
    });
}


//保存信息
function saveDdjAdmin(data,callback){
    delete  data.createdate;
    HAF.ajax({
        url: HAF.basePath() + "/ddj/ddjAdmin/saveOrUpdate.do",
        contentType : 'application/json',
        data: HAF.stringify(data),
        success: function(rdata){
            if(rdata){
                $.extend(data, rdata);
                if(callback) callback(data);
            }else{
                alert("操作失败，未保存成功");
            }
        }
    });
}

//删除信息
function deleteDdjAdmin(ids,callback){
    HAF.ajax({
        url: HAF.basePath()+"/ddj/ddjAdmin/deleteByIds.do",
        contentType : 'application/json',
        data: HAF.stringify(ids),
        success: function(data){
            if(data && data.success){
                if(callback) callback();
            }
        }
    });
}