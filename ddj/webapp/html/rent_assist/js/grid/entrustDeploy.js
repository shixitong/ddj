//初始化信息
var $entrustDeployGrid;
$(function (){
    var gridOptions = {
        width: "100%",
        height:"540px",
        singleSelect:true,
        pagination:true,
        rownumbers:true,
        fitColumns:true,
        border:true,
        url: HAF.basePath()+"/ddj/entrustDeploy/list/getAllList.do",
        //queryParams: {appId: id},
        searchParams: [
            {name:"type",type:"hidden"},
            {name:"iscontact",type:"hidden"},

            {label: "委托类型",labelWidth:"2",name:"typeText",type:"downlist",readonly:false,options:{
                data:[["","全部类型"],["1","出租"],["2","求租"]],
                valueField:"[name='type']",textField:"[name='typeText']",editable:false
            }},
            {name:"name",label: " 联系人",type:"text"},
            {name:"mobile",label: " 联系电话",type:"text"},
            {label: "状态",labelWidth:"2",name:"iscontactText",type:"downlist",readonly:false,options:{
                data:[["","全部类型"],["1","出租"],["2","求租"]],
                valueField:"[name='iscontact']",textField:"[name='iscontactText']",editable:false
            }}
        ],
        columns: [
            {field:'id',checkbox:true},
            {field:'type',title:'委托类型',width:90,align:'center',formatter:function(val,row,index){
                if(val==1){
                    return "出租";
                }else if(val==2){
                    return "求租";
                }
              }
            },
            {field:'name',title:'联系人',width:90,align:'center'},
            {field:'mobile',title:'联系电话',width:90,align:'center'},
            {field:'description',title:'描述',width:200,align:'center'},
            {field:'iscontact',title:'状态',width:90,align:'center',formatter:function(val,row,index){
                if(val==0){
                    return "<label style='color: red'>未联系</label>";
                }else if(val==1){
                    return  "<label style='color:green'> 已联系</label>";
                }
              }
            },//（0未联系/1已联系）
            {field:'remark',title:'备注（是否靠谱）',width:150,align:'center'}
        ],
        toolbar: ['-',{
            iconCls: 'icon-ok',
            text: "已联系",
            handler: function(){
                var rows = $entrustDeployGrid.datagrid("getSelected");
                if(rows!=null){
                    editEntrustDeploy("添加备注", function(data){
                        saveEntrustDeploy(data, function(data){
                            $entrustDeployGrid.datagrid("reload");
                        });
                    },rows);
                }else{
                  alert("请选择一行数据！");
                }
            }
        }
        ]
    };
	
	//$.extend(gridOptions,{
    //    toolbar: [{},{}]
	//});

    $entrustDeployGrid = HAF.Form.searchGrid("#entrustDeployGrid",gridOptions);
});


//编辑信息
function editEntrustDeploy(title, callback, row){

    HAF.Form.gridEditor({
        title: title,
        hidden:["id"],
        visible: [
            [
			    {label: "备注（是否靠谱）",labelWidth:"2",name:"remark",fieldWidth:"10",type:"textarea", readonly: false,options:{}}
		     ]
        ],
        rowData: row,
        callback: callback
    });
}


//保存信息
function saveEntrustDeploy(data,callback){
    HAF.ajax({
        url: HAF.basePath() + "/ddj/entrustDeploy/saveOrUpdate.do",
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
function deleteEntrustDeploy(ids,callback){
    HAF.ajax({
        url: HAF.basePath()+"/ddj/entrustDeploy/deleteByIds.do",
        contentType : 'application/json',
        data: HAF.stringify(ids),
        success: function(data){
            if(data && data.success){
                if(callback) callback();
            }
        }
    });
}