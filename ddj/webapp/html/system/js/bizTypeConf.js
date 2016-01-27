//初始化信息
var $bizTypeConfGrid;
$(function (){
    var gridOptions = {
        width: "100%",
        height:"540px",
        singleSelect:true,
        pagination:true,
        rownumbers:true,
        border:true,
        fitColumns:true,
        url: HAF.basePath()+"/oa/service/conf/list/getAllList.do",
        //queryParams: {appId: id},
        searchParams: [
            {name:"type",type:"hidden",defaultValue:"stu-self-type"},
            {name:"text",label: "类型"}
        ],
        columns: [
            {field:'text',title:'类型',width:80,align:'center'},
            {field:'description',title:'描述',width:250,align:'center'}
        ],
        onDblClickRow: function(rowIndex, rowData){
            window.open(HAF.basePath()+"/oa/service/conf/edit.do?id="+rowData.id);
        }
    };

    $bizTypeConfGrid = HAF.Form.searchGrid("#bizTypeConfGrid",gridOptions);
});


