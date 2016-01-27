//初始化信息
var $quotationGrid;
$(function (){
    var gridOptions = {
        width: "100%",
        height:"540px",
        singleSelect:true,
        pagination:true,
        rownumbers:true,
        fitColumns:true,
        border:true,
        url: HAF.basePath()+"/ddj/quotation/list/getAllList.do",
        //queryParams: {appId: id},
        searchParams: [
            {name:"userPhone",type:"hidden",defaultValue:userPhone},
            {name:"product",label: " 产品名称",type:"text"},
            {field:'productType',label:'产品镀种',type:"text"}
        ],
        columns: [
            {field:'id',checkbox:true},
          //  {field:'userPhone',title:'公司手机号',width:90,align:'center'},
            {field:'product',title:'产品名称',width:90,align:'center'},
            {field:'productType',title:'产品镀种',width:90,align:'center'},
            {field:'texture',title:'产品材质',width:90,align:'center'},
            {field:'price',title:'单价',width:90,align:'center'},
            {field:'createDate',title:'创建时间',width:90,align:'center'}
        ],
        toolbar: ['-',{
            text: "新增",
            iconCls: 'icon-add',
            handler: function(){
                editQuotation("添加", function(data){
                    saveQuotation(data, function(data){
                        $quotationGrid.datagrid("appendRow",data).datagrid("acceptChanges");
                    });
                });
            }
        }, '-',{
            iconCls: 'icon-remove',
            text: "删除",
            handler: function(){
            if (window.confirm("是否执行此操作?")) {
                var rows = $quotationGrid.datagrid("getChecked");
                if(rows.length==0){
                    alert("请选择需要删除的行");
                    return false;
                }
                var ids = [];
                $.each(rows,function(i,row){
                    if(row.id) ids.push(row.id);
                    var index = $quotationGrid.datagrid("getRowIndex",row);
                    $quotationGrid.datagrid("deleteRow",index).datagrid("acceptChanges");
                });
                //
                if(ids.length){
                    deleteQuotation(ids);
                }
            }

            }
        }],
        onDblClickRow: function(rowIndex, rowData){
            editQuotation("信息", function(data){
                saveQuotation(data, function(data){
                    $quotationGrid.datagrid("updateRow",{index:rowIndex,row:data})
                        .datagrid("acceptChanges");
                });
            }, rowData);
        }
    };
	
	//$.extend(gridOptions,{
    //    toolbar: [{},{}]
	//});

    $quotationGrid = HAF.Form.searchGrid("#quotationGrid",gridOptions);
});


//编辑信息
function editQuotation(title, callback, row){

    HAF.Form.gridEditor({
        title: title,
        visible: [[
                {label: "产品名称",labelWidth:"2",name:"product",fieldWidth:"4",type:"text", readonly: false,options:{}},
               {label: "产品镀种",labelWidth:"2",name:"productType",fieldWidth:"4",type:"text", readonly: false,options:{}}
            ],
            [
                {label: "产品材质",labelWidth:"2",name:"texture",fieldWidth:"4",type:"text", readonly: false,options:{}},
                {label: "单价",labelWidth:"2",name:"price",fieldWidth:"4",type:"text", readonly: false,options:{}} ,
            ]
         ],
        rowData: row,
        callback: callback
    });
}


//保存信息
function saveQuotation(data,callback){

    data.userPhone=userPhone;

    HAF.ajax({
        url: HAF.basePath() + "/ddj/quotation/saveOrUpdate.do",
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
function deleteQuotation(ids,callback){
    HAF.ajax({
        url: HAF.basePath()+"/ddj/quotation/deleteByIds.do",
        contentType : 'application/json',
        data: HAF.stringify(ids),
        success: function(data){
            if(data && data.success){
                if(callback) callback();
            }
        }
    });
}