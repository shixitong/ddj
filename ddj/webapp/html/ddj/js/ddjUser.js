//初始化信息
var $ddjUserGrid;
var serviceUrl= HAF.basePath()+"/images/";


$(function (){
    var gridOptions = {
        width: "100%",
        height:"540px",
        singleSelect:true,
        pagination:true,
        rownumbers:true,
        fitColumns:true,
        border:true,
        url: HAF.basePath()+"/ddjUser/list/getUserList.do",
        //queryParams: {appId: id},
        searchParams: [
            {name:"usertype",type:"hidden"},
            {label: "类型",labelWidth:"2",name:"typeText",type:"downlist",readonly:false,options:{
                data:[["","全部类型"],["1","客户"],["2","厂家"]],
                valueField:"[name='usertype']",textField:"[name='typeText']",editable:false
             }
            },
            {name:"audit",type:"hidden"},
            {label: "审核",labelWidth:"2",name:"statusText",type:"downlist",readonly:false,options:{
                data:[["","全部类型"],["0","未审核"],["1","通过"]],
                valueField:"[name='audit']",textField:"[name='statusText']",editable:false
            }
            },
            {name:"telephone",label: " 手机号",type:"text"},
            {name:"username",label: "姓名",type:"text"}
        ],
        columns: [
            {field:'userid',checkbox:true},
            {field:'usertype',title:'类型',width:60,align:'center',formatter:function(val,row,index){//1客户2仓库3厂家4司机
                if(val==1){
                    return "客户";
                }else if(val==2){
                    return "厂家";
                }
            }},
            {field:'telephone',title:'手机号',width:120,align:'center'},
            {field:'username',title:'姓名',width:60,align:'center'},
            {field:'companyname',title:'公司名称',width:120,align:'center'},
            {field:'idcard',title:'身份证号',width:150,align:'center'},
            {field:'sfzzmpic',title:'身份证正页',width:90,align:'center',formatter:function(val,row,index){

                var html="";
                if(!HAF.isEmpty(val)){
                    var phs=val.split(",");
                    for(var i=0;i<phs.length;i++){
                        var url=serviceUrl+phs[i];
                        var img='<a href="javascript:void(0)"><img onclick="openLink(this)" style="height:40px;width:40px" data-id="'+phs[i]+'" src="'+url+'" /></a>';
                        html+=img;
                    }
                }
                return html;
            }
            },
            {field:'sfzfmpic',title:'身份证反页',width:90,align:'center',formatter:function(val,row,index){

                var html="";
                if(!HAF.isEmpty(val)){
                    var phs=val.split(",");
                    for(var i=0;i<phs.length;i++){
                        var url=serviceUrl+phs[i];
                        var img='<a href="javascript:void(0)"><img onclick="openLink(this)" style="height:40px;width:40px" data-id="'+phs[i]+'" src="'+url+'" /></a>';
                        html+=img;
                    }
                }
                return html;
            }
            },
            {field:'logo',title:'公司标志',width:90,align:'center'},
           
            {field:'intro',title:'公司简介',width:90,align:'center'},
            {field:'jyfw',title:'经营范围',width:90,align:'center'},
            {field:'address',title:'地址',width:90,align:'center'},
            {field:'pfen',title:'评分',width:90,align:'center'},
            {field:'sortid',title:'排序',width:90,align:'center'},
            {field:'audit',title:'审核状态',width:90,align:'center',formatter:function(val,row,index){
                if(val==0){
                    return "未审核";
                }else if(val==1){
                    return "通过";
                }
            }}
        ],
        toolbar: ['-',{
            text: "审核通过",
            iconCls: 'icon-ok',
            handler: function(){
                var rows = $ddjUserGrid.datagrid("getChecked");
                if(rows.length==0){
                    alert("请选择需要审核的用户");
                    return false;
                }
                var userids = [];
                $.each(rows,function(i,row){
                    if(row.userid)
                    	userids.push(row.userid);
                });
                if (window.confirm("确定审核通过吗?")) {
                    saveDdjUser(userids, function(data){
                        $ddjUserGrid.datagrid("reload");
                    });
                }
            }
        },'-',{
            text: "审核未通过",
            iconCls: 'icon-ok',
            handler: function(){
                var rows = $ddjUserGrid.datagrid("getChecked");
                if(rows.length==0){
                    alert("请选择需要审核的用户");
                    return false;
                }
                var userids = [];
                $.each(rows,function(i,row){
                    if(row.userid)
                    	userids.push(row.userid);
                });
                if (window.confirm("确定审核未通过吗?")) {
                	auditUserForNo(userids, function(data){
                        $ddjUserGrid.datagrid("reload");
                    });
                }
            }
        }
        ],
        onDblClickRow: function(rowIndex, rowData){
            
        }
    };

    $ddjUserGrid = HAF.Form.searchGrid("#ddjUserGrid",gridOptions);
});


//编辑信息
function editDdjUser(title, callback, row){

    HAF.Form.gridEditor({
        title: title,
        visible: [
            [
			    {label: "公司名称",labelWidth:"2",name:"companyName",fieldWidth:"4",type:"text", readonly: true,options:{}} ,
                {label: "姓名",labelWidth:"2",name:"userName",fieldWidth:"4",type:"text", readonly: true,options:{}}
            ],
            [
			    {label: "评分",labelWidth:"2",name:"grade",fieldWidth:"4",type:"text", readonly: false,options:{}},
                {label: "排序",labelWidth:"2",name:"orderNo",fieldWidth:"4",type:"text", readonly: false,options:{}}
            ]
        ],
        rowData: row,
        callback: callback
    });
}


//保存信息
function saveDdjUser(userids,callback){
    HAF.ajax({
        url: HAF.basePath() + "/ddjUser/updateUserAudit.do",
        contentType : 'application/json',
        data: HAF.stringify(userids),
        success: function(rdata){
        	if(rdata && rdata.success){
                if(callback) callback();
            }else{
                alert("操作失败，未保存成功");
            }
        }
    });
}

//审核未通过
function auditUserForNo(userids,callback){
    HAF.ajax({
        url: HAF.basePath() + "/ddjUser/auditUserForNo.do",
        contentType : 'application/json',
        data: HAF.stringify(userids),
        success: function(rdata){
        	if(rdata && rdata.success){
                if(callback) callback();
            }else{
                alert("操作失败，未保存成功");
            }
        }
    });
}

//删除信息
function deleteDdjUser(ids,callback){
    HAF.ajax({
        url: HAF.basePath()+"/ddj/ddjUser/deleteByIds.do",
        contentType : 'application/json',
        data: HAF.stringify(ids),
        success: function(data){
            if(data && data.success){
                if(callback) callback();
            }
        }
    });
}


/*
 打开图片
 */
function openLink(key){
    window.open($(key).attr("src"));
}
