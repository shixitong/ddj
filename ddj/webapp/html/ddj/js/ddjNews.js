//初始化信息
var $ddjNewsGrid;
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
        url: HAF.basePath()+"/ddj/ddjNews/list/getAllList.do",
        //queryParams: {appId: id},
        searchParams: [
            {name:"type",type:"hidden"},
            {label: "类型",labelWidth:"2",name:"typeText",type:"downlist",readonly:false,options:{
                data:[["","全部类型"],["1","滚动广告"],["2","新闻列表"]],
                valueField:"[name='type']",textField:"[name='typeText']",editable:false
            }},
            {name:"title",label: " 标题",type:"text"}
        ],
        columns: [
            {field:'id',checkbox:true},
            {field:'type',title:'类型',width:40,align:'center',formatter:function(val,row,index){//（1滚动广告2新闻列表）
                if(val==1){
                    return "滚动广告";
                }else if(val==2){
                    return "新闻列表";
                }
            }},
            {field:'photo',title:'图片展示',width:150,align:'center',formatter:function(val,row,index){

                var html="<button  onclick='addImg(\""+ val+"_"+row.id +"\")'>添加</button>&nbsp;&nbsp;&nbsp;&nbsp;";
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
            {field:'title',title:'标题',width:120,align:'center'},
            {field:'remark',title:'概括',width:250,align:'center'},
            {field:'detail',title:'详情',width:90,align:'center',formatter:function(val,row,index){
                var html="<a  href='javascript:void(0)' onclick='showNewsDetail(\""+row.id +"\")'>查看</a>";
                return html;
             }
            },
            {field:'createTime',title:'创建时间',width:90,align:'center'}
        ],
        toolbar: ['-',{
            iconCls: 'icon-add',
            text: "新增",
            handler: function(){
                editDdjNews("添加", function(data){
                    saveDdjNews(data, function(data){
                        $ddjNewsGrid.datagrid("appendRow",data).datagrid("acceptChanges");
                    });
                });
            }
        }, '-',{
            iconCls: 'icon-remove',
            text: "删除",
            handler: function(){
            if (window.confirm("是否执行此操作?")) {
                var rows = $ddjNewsGrid.datagrid("getChecked");
                if(rows.length==0){
                    alert("请选择需要删除的行");
                    return false;
                }
                var ids = [];
                $.each(rows,function(i,row){
                    if(row.id) ids.push(row.id);
                    var index = $ddjNewsGrid.datagrid("getRowIndex",row);
                    $ddjNewsGrid.datagrid("deleteRow",index).datagrid("acceptChanges");
                });
                //
                if(ids.length){
                    deleteDdjNews(ids);
                }
            }

            }
        },'-',{
            text: "编辑详情",
            iconCls: 'icon-edit',
            handler: function(){
                var rows = $ddjNewsGrid.datagrid("getSelected");
                if(rows!=null){
                    window.open(HAF.basePath()+"/ddj/ddjNews/edit.do?id="+rows.id);
                }else{
                    alert("请选择一条数据");
                }
            }
        }
        ],
        onDblClickRow: function(rowIndex, rowData){
            editDdjNews("信息", function(data){
                saveDdjNews(data, function(data){
                    $ddjNewsGrid.datagrid("reload");
                });
            }, rowData);
        }
    };
	
	//$.extend(gridOptions,{
    //    toolbar: [{},{}]
	//});

    $ddjNewsGrid = HAF.Form.searchGrid("#ddjNewsGrid",gridOptions);
});


//编辑信息
function editDdjNews(title, callback, row){

    HAF.Form.gridEditor({
        title: title,
        //hidden:["type"],
        visible: [
            [
			    {label: "类型",labelWidth:"2",name:"type",fieldWidth:"4",type:"downlist", readonly: false,options:{
                    data:[["1","广告"],["2","新闻"]],
                    valueField:"input[name='type']",
                    onSelect:function(val){
                        if(val=="1"){
                            $("textarea[name='remark']").attr("readonly",true);
                        }else{
                            $("textarea[name='remark']").attr("readonly",false);
                        }
                    }
                }
                },
                {label: "标题",labelWidth:"2",name:"title",fieldWidth:"4",type:"text", readonly: false,options:{}}
            ],
            [
                {label: "概括",labelWidth:"2",name:"remark",fieldWidth:"10",type:"textarea", readonly: false,options:{}}
            ]
        ],
        rowData: row,
        callback: callback
    });
}


//保存信息
function saveDdjNews(data,callback){
    HAF.ajax({
        url: HAF.basePath() + "/ddj/ddjNews/saveOrUpdate.do",
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
function deleteDdjNews(ids,callback){
    HAF.ajax({
        url: HAF.basePath()+"/ddj/ddjNews/deleteByIds.do",
        contentType : 'application/json',
        data: HAF.stringify(ids),
        success: function(data){
            if(data && data.success){
                if(callback) callback();
            }
        }
    });
}


/**
 * 添加图片
 */
function addImg(val){
    var str=val.split("_");
    var da={};
    var id=str[1];
    var photo=str[0];
    da.id=id;

    var upload_url = HAF.basePath() + "/oa/service/attach/upload.do";
    HAF.Form.fileUpload({
        url: upload_url,
        title:"照片上传",
        allowTypes: ["jpg","png","gif","bmp","jpeg"],
        maxSize: 2000,
        singleSelect: false,
        editable:true,
        require:true,
        success: function(data){
            var  idPhoto=data[0].id;
              da.photo=idPhoto;

            saveDdjNews(da,function(){
                $ddjNewsGrid.datagrid("reload");
            });
        }
    });
}

/*
 打开图片
 */
function openLink(key){
    window.open($(key).attr("src"));
}

/**
 * 编辑新闻详情
 * @param id
 */
function showNewsDetail(id){
    window.open(HAF.basePath()+"/ddj/ddjNews/detail.do?id="+id);

}