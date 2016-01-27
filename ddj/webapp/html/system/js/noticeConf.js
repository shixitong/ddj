//初始化信息
var $noticeConfGrid;
$(function (){
    var gridOptions = {
        width: "100%",
        height:"540px",
        singleSelect:true,
        pagination:true,
        rownumbers:true,
        fitColumns:true,
        border:true,
        url: HAF.basePath()+"/springmvc/noticeConf/list/getAllList.do",
        //queryParams: {appId: id},
        searchParams: [
            {name:"startTime",label: "创建时间(开始)",type:'date'},
            {name:"endTime",label: "创建时间(结束)",type:'date'},
            {name:"title",label: "标题"}
        ],
        columns: [
            {field:'id',checkbox:true},
            {field:'title',title:'标题',width:150,align:'center'},
            {field:'startTime',title:'开始时间',width:90,align:'center'},
            {field:'endTime',title:'结束时间',width:90,align:'center'},
            {field:'typeName',title:'类型名称',width:90,align:'center'},
            {field:'modelName',title:'模块名称',width:90,align:'center'},
            {field:'showTime',title:'显示时间',width:90,align:'center'},
            {field:'content',title:'内容',width:200,align:'center',formatter:function(val,row,index){
                if(val!=null&&val.length>30){
                    return val.substr(0,30)+"…";
                }
                return val;
              }
            },
            {field:'createDate',title:'创建时间',width:90,align:'center'}
        ],
        toolbar: ['-',{
            iconCls: 'icon-add',
            text: "新增",
            handler: function(){
                window.open(HAF.basePath()+"/springmvc/noticeConf/edit.do");
            }
        }, '-',{
            iconCls: 'icon-remove',
            text: "删除",
            handler: function(){
            if (window.confirm("是否执行此操作?")) {
                var rows = $noticeConfGrid.datagrid("getChecked");
                if(rows.length==0){
                    alert("请选择需要删除的行");
                    return false;
                }
                var ids = [];

                for (var i = 0; i < rows.length; i++) {
                    var row = rows[i];
                    if (row.id) ids.push(row.id);
                  //  $noticeConfGrid.datagrid("deleteRow",index).datagrid("acceptChanges");
                }
                //
                if(ids.length){
                    deleteNoticeConf(ids,function(){
                         $noticeConfGrid.datagrid("reload");
                    });
                }
            }

            }
        }],
        onDblClickRow: function(rowIndex, rowData){
            window.open(HAF.basePath()+"/springmvc/noticeConf/edit.do?id="+rowData.id);
        }
    };

    $noticeConfGrid = HAF.Form.searchGrid("#noticeConfGrid",gridOptions);
});



//保存信息
function saveNoticeConf(data,callback){
    HAF.ajax({
        url: HAF.basePath() + "/springmvc/noticeConf/saveOrUpdate.do",
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
function deleteNoticeConf(ids,callback){
    HAF.ajax({
        url: HAF.basePath()+"/springmvc/noticeConf/deleteByIds.do",
        contentType : 'application/json',
        data: HAF.stringify(ids),
        success: function(data){
            if(data && data.success){
                if(callback) callback();
            }
        }
    });
}