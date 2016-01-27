//初始化信息

var timeOut="";

var $stationinfoGrid;
$(function (){
    var gridOptions = {
        width: "100%",
        height:"540px",
        singleSelect:false,
        pagination:true,
        rownumbers:true,
        fitColumns:true,
        border:true,
        url: HAF.basePath()+"/stationinfo/list/getAllList.do",
        pagination: true,
        pageSize:10,
        pageList:[10],
        searchParams: [
            {name:"stationType",label: "园区类型",type:"hidden"},
          //  {name:"order",label: "园区类型",type:"hidden",defaultValue:"0"},
            {name:"company",label: "园区名称",type:"text"},
            {name:"addressDetail",label: "园区地址",type:"text"},
            {name:"stationtypeText",label: "园区类型",type:"downlist" ,readonly:false,options:{
                data:[["1","出租"],["2","求租"],["3","办公室"],["4","孵化器"]],
                valueField:"[name='stationType']",textField:"[name='stationtypeText']",editable:false
              }
            }
        ],
        columns: [
            {field:'id',checkbox:true},
            {field:'company',title:'园区名称',width:90,align:'center'},
            {field:'address',title:'地址',width:200,align:'center'},
           // {field:'userid',title:'用户id',width:60,align:'center'},
            {field:'stationtype',title:'园区类型',width:60,align:'center',formatter:function(val,row,index){
                if(val==1){
                    return "出租";
                }else if(val==2){
                    return "求租";
                }else if(val==3){
                    return "办公室";
                }else if(val==4){
                    return "孵化器";
                }
            }},//（1出租2求助3办公室4免费孵化器工位）
           // {field:'phonenum',title:'发布人电话号码',width:120,align:'center'},/*（冗余没用）*/
          /*  {field:'visible',title:'联系人电话',width:90,align:'center',formatter:function(val,row,index){
                if(val==0){
                    return "不可见";
                }else if(val==1){
                    return "可见";
                }
            }},//可见*/
            {field:'square',title:'总面积',width:90,align:'center',formatter:function(val,row,index){
                if(val==3){//（3有效）
                    return "有效";
                }
                return val;
            }},
            {field:'number',title:'数量',width:90,align:'center'},
            {field:'price',title:'起租单价',width:60,align:'center'},
            {field:'communicate',title:'咨询电话',width:120,align:'center'},
            {field:'time',title:'发布时间',width:120,align:'center'},
            {field:'contactName',title:'联系人',width:90,align:'center'},
            {field:'phonenum',title:'联系电话',width:90,align:'center'},
            {field:'description',title:'描述',width:120,align:'center'},
            {field:'remark',title:'即刻点评',width:120,align:'center'}
        ],
        toolbar: ['-',{
            text: "新增园区",
            iconCls: 'icon-add',
            handler: function(){
               window.open(HAF.basePath()+"/static/form/index.do");
            }
        }, '-',{
            text: "删除园区",
            iconCls: 'icon-remove',
            handler: function(){
            if (window.confirm("是否执行此操作?")) {
                var rows = $stationinfoGrid.datagrid("getChecked");
                if(rows.length==0){
                    alert("请选择需要删除的行");
                    return false;
                }
                var ids = [];
                $.each(rows,function(i,row){
                    if(row.id) ids.push(row.id);
                   // var index = $stationinfoGrid.datagrid("getRowIndex",row);
                  //  $stationinfoGrid.datagrid("deleteRow",index).datagrid("acceptChanges");
                });
                //
                if(ids.length){
                    deleteStationinfo(ids,function(){
                        $stationinfoGrid.datagrid("reload");
                    });
                }
            }

            }
          }, '-',{
            text: "新增工位/办公室",
            iconCls: 'icon-add',
            handler: function(){
                var rows = $stationinfoGrid.datagrid("getSelected");
               if(rows!=null){
                   window.open(HAF.basePath()+"/static/form/index2.do?id="+rows.id);
               }else{
                   alert("请选择一条数据");
               }
            }
        }
        ],
        onDblClickRow: function(rowIndex, rowData){
            editStationinfo("编辑园区基本信息", function(data){
                saveStationinfo(data, function(data){
                    $stationinfoGrid.datagrid("updateRow",{index:rowIndex,row:data})
                        .datagrid("acceptChanges");
                });
            }, rowData);
        }
    };
	
	//$.extend(gridOptions,{
    //    toolbar: [{},{}]
	//});

    $stationinfoGrid = HAF.Form.searchGrid("#stationinfoGrid",gridOptions);
});


//编辑信息
function editStationinfo(title, callback, row){

    HAF.Form.gridEditor({
        hidden:["latitude","longitude","id"],
        title: title,
        visible: [
            [
                {label: "园区名称",labelWidth:"2",name:"company",fieldWidth:"4",type:"text", readonly: false,options:{}},
                {label: "出租类型",labelWidth:"2",name:"stationtype",fieldWidth:"4",type:"downlist", readonly: false,options:{
                    data:[["1","工位"],["3","办公室"]],
                    valueField:"input[name='stationtype']"
                   }
                }//（1出租2求助3办公室4免费孵化器工位）
            ],
            [
                {label: "地址（区）",labelWidth:"2",name:"area",fieldWidth:"4",type:"text", readonly: false,options:{}},
                {label: "地址（街道）",labelWidth:"2",name:"street",fieldWidth:"4",type:"text", readonly: false,options:{}}
            ],
            [
                {label: "详细地址",labelWidth:"2",name:"address",fieldWidth:"10",type:"text", readonly: false,options:{
                    textField:"input[name='address']",
                    editable:true,
                    onSelect:function(val,text){
                        if(val!=""){
                            var location=val.split(",");
                            if(location.length>1){
                                $("input[name='latitude']").val(location[0]);
                                $("input[name='longitude']").val(location[1]);
                            }
                        }
                    },
                    onChange:function(val){
                        clearTimeout(timeOut);
                        if(val!=""){
                            timeOut=setTimeout(searchAddress,500);
                        }
                    }
                }}
            ],
            [
                {label: "总面积",labelWidth:"2",name:"square",fieldWidth:"4",type:"text", readonly: false,options:{}} ,
                {label: "数量",labelWidth:"2",name:"number",fieldWidth:"4",type:"text", readonly: false,options:{}}
            ],
            [
			    {label: "起租单价",labelWidth:"2",name:"price",fieldWidth:"4",type:"text", readonly: false,options:{}} ,
                {label: "咨询电话",labelWidth:"2",name:"communicate",fieldWidth:"4",type:"text", readonly: false,options:{}}
            ],
            [
			    {label: "描述",labelWidth:"2",name:"description",fieldWidth:"10",type:"textarea", readonly: false,options:{}}
            ],
            [
                {label: "即刻点评",labelWidth:"2",name:"remark",fieldWidth:"10",type:"textarea", readonly: false,options:{}}
            ]
        ],
        onload:function($d){

            $($d).find("input[name='address']").keyup(function(){
                var val=$(this).val();
                clearTimeout(timeOut);
                if(val!=""){
                    timeOut=setTimeout(function(){
                        searchAddress(val);
                    },500);
                }
            });
        },
        rowData: row,
        callback: callback
    });
}


//保存信息
function saveStationinfo(data,callback){
    HAF.ajax({
        url: HAF.basePath() + "/stationinfo/saveOrUpdate.do",
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
function deleteStationinfo(ids,callback){
    HAF.ajax({
        url: HAF.basePath()+"/stationinfo/deleteByIds.do",
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
 * 百度地图坐标点拾取
 * @param search
 */
function searchAddress(search){

    $.ajax({
        url:"http://api.map.baidu.com/place/v2/suggestion?query="+search+"&region=131&output=json&ak=3ecea51f560650b1ed8a4b99808f52e8",
        dataType: "jsonp",
        type:"get",
        success: function(rdata){
            var result=rdata.result;

            var textValue=[];
            for(var i=0;i<result.length;i++){
                var text=[];
                var location={};
                location=result[i].location;
                if(location!=null&&(location.lat!=null&&location.lng!=null)){
                    text.push(location.lat+','+location.lng);
                }
                text.push(result[i].name);

                textValue.push(text);
            }

            // alert(HAF.stringify(rdata));
            HAF.Form.downList("input[name='address']",{//（1出租2求助3办公室4免费孵化器工位）
                editable:true,
                data:textValue,
                textField:"input[name='address']",
                onSelect:function(val,text){
                    if(val!=""){
                        var location=val.split(",");
                        if(location.length>1){
                            $("input[name='latitude']").val(location[0]);
                            $("input[name='longitude']").val(location[1]);
                        }
                    }
                },
                onChange:function(val){
                    clearTimeout(timeOut);
                    if(val!=""){
                        timeOut=setTimeout(searchAddress,500);
                    }
                }
            })
        }
    });


}
