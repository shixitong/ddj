//初始化信息
$(function (){
    HAF.Form.init({
        defButtons: false,
        buttons:[
            {label:"保存",clsName:"btn-success",handle:function(){
                var noticeConf = {};
                $("#noticeConf").find("input:text,input:hidden,textarea").filter("[name]:enabled").each(function(){
                    noticeConf[this.name] = this.value;
                })
                editor1.sync();//先同步，才能拿到值
                var content1=$("#content1").val();
                noticeConf["content"] = editor1.html();

                saveNoticeConf(noticeConf,function(){
                    alert("保存成功");
                    HAF.closeWindow();
                });
            }
            },
            {label:"预览",clsName:"btn-info",handle:function(){
                var noticeConf = {};
                $("#noticeConf").find("input:text,input:hidden,textarea").filter("[name]:enabled").each(function(){
                    noticeConf[this.name] = this.value;
                })
                editor1.sync();//先同步，才能拿到值
                var content1=$("#content1").val();
                noticeConf["content"] = editor1.html();

                HAF.footPopover({
                    width:375,
                    height: 250,
                    title: noticeConf.title,
                    content: noticeConf.content,
                    style: "primary",
                    showTimeOut: 3000,
                    hideTimeOut: 1000*60*30,
                    timeInterval: 1
                });


            }
            },
            {label:"退出",clsName:"btn-warning",handle:function(){
                HAF.closeWindow();
            }
            }
        ]
    }).datepicker().downList();

    HAF.Form.downList("#modelName",{
        queryParams:{type:'notice-model-type'},
        textField:"#modelName",valueField:"#modelId"

    }).downList("#typeName",{
        queryParams:{type:'notice-type'},
        textField:"#typeName",valueField:"#typeId"

    })

});



//保存信息
function saveNoticeConf(data,callback){
    HAF.ajax({
        url: HAF.basePath() + "/springmvc/noticeConf/saveOrUpdate.do",
        //contentType : 'application/json',
        data:data,
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

