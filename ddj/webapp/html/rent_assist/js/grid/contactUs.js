//初始化信息
var $contactUsGrid;
$(function (){
    var gridOptions = {
        width: "100%",
        height:"540px",
        singleSelect:true,
        pagination:true,
        rownumbers:true,
        fitColumns:true,
        border:true,
        url: HAF.basePath()+"/ddj/contactUs/list/getAllList.do",
        //queryParams: {appId: id},
        searchParams: [
            {name:"stationName",label: "园区名称",type:"text"},
            //  {name:"order",label: "园区类型",type:"hidden",defaultValue:"0"},
            {name:"mobile",label: "联系电话",type:"text"}
        ],
        columns: [
            {field:'id',checkbox:true},
            {field:'type',title:'预约类型',width:90,align:'center',formatter:function(val,row,index){
                if(val==1){
                    return "出租";
                }
              }
            },
            {field:'stationName',title:'园区名称',width:90,align:'center'},
            {field:'name',title:'联系人',width:90,align:'center'},
            {field:'mobile',title:'联系电话',width:90,align:'center'},
            {field:'description',title:'描述',width:250,align:'center'}
        ]
    };

    $contactUsGrid = HAF.Form.searchGrid("#contactUsGrid",gridOptions);
});
