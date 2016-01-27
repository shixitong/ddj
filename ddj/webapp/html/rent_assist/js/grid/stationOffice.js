//初始化信息
var $stationOfficeGrid;
$(function (){
    var gridOptions = {
        width: "100%",
        height:"450px",
        singleSelect:true,
        pagination:true,
        rownumbers:true,
        fitColumns:true,
        border:true,
        url: HAF.basePath()+"/ddj/stationOffice/list/getAllList.do",
        defaultParams: {appId:appId},
        columns: [
            {field:'id',checkbox:true},
            {field:'num',title:'数量/面积',width:90,align:'center'},
            {field:'price',title:'价格',width:90,align:'center'},
            {field:'photo',title:'照片',width:200,align:'left',formatter:function(val,row,index){

                var html="<button  onclick='addImg(\""+ val+"_"+row.id +"\")'>添加</button>&nbsp;&nbsp;&nbsp;&nbsp;";
                if(!HAF.isEmpty(val)){
                    var phs=val.split(",");
                    for(var i=0;i<phs.length;i++){
                        var url=serviceUrl+phs[i];
                        var img='<a href="javascript:void(0)"><img onclick="openLink(this)" style="height:50px;width:50px" data-id="'+phs[i]+'" src="'+url+'" /></a>';
                         html+=img;
                    }
                }
                return html;
              }
             }
        ],
        toolbar: ['-',{
            iconCls: 'icon-add',
            text: "新增",
            handler: function(){
                editStationOffice("添加", function(data){

                    data.stationId=appId;
                    saveStationOffice(data, function(data){
                        $stationOfficeGrid.datagrid("appendRow",data).datagrid("acceptChanges");
                    });
                });
            }
        }, '-',{
            iconCls: 'icon-remove',
            text: "删除",
            handler: function(){
            if (window.confirm("是否执行此操作?")) {
                var rows = $stationOfficeGrid.datagrid("getChecked");
                if(rows.length==0){
                    alert("请选择需要删除的行");
                    return false;
                }
                var ids = [];
                $.each(rows,function(i,row){
                    if(row.id) ids.push(row.id);
                    var index = $stationOfficeGrid.datagrid("getRowIndex",row);
                    $stationOfficeGrid.datagrid("deleteRow",index).datagrid("acceptChanges");
                });
                //
                if(ids.length){
                    deleteStationOffice(ids);
                }
            }

            }
        }],
        onDblClickRow: function(rowIndex, rowData){
            editStationOffice("信息", function(data){
                saveStationOffice(data, function(data){
                    $stationOfficeGrid.datagrid("updateRow",{index:rowIndex,row:data})
                        .datagrid("acceptChanges");
                });
            }, rowData);
        }
    };
	
	//$.extend(gridOptions,{
    //    toolbar: [{},{}]
	//});

    $stationOfficeGrid = HAF.Form.searchGrid("#stationOfficeGrid",gridOptions);
});


//编辑信息
function editStationOffice(title, callback, row){

    HAF.Form.gridEditor({
        title: title,
        visible: [
            [
			    {label: "数量/面积",labelWidth:"2",name:"num",fieldWidth:"4",type:"text", readonly: false,options:{}} ,
                {label: "价格",labelWidth:"2",name:"price",fieldWidth:"4",type:"text", readonly: false,options:{}}
            ]
         ],
        rowData: row,
        callback: callback
    });
}


//保存信息
function saveStationOffice(data,callback){
    HAF.ajax({
        url: HAF.basePath() + "/ddj/stationOffice/saveOrUpdate.do",
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
function deleteStationOffice(ids,callback){
    HAF.ajax({
        url: HAF.basePath()+"/ddj/stationOffice/deleteByIds.do",
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
            if(photo==null||photo=="null"||photo=="undefined"){
                da.photo=idPhoto;
            }else{
                photo+=","+idPhoto;
                da.photo=photo;
            }
            saveStationOffice(da,function(){
                $stationOfficeGrid.datagrid("reload");
            });
        }
    });
}