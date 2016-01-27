//初始化信息
$(function (){
    HAF.Form.init({
        defButtons: false,
        buttons:[
            {label:"保存",clsName:"btn-success",handle:function(){
                var bizTypeConf = {};
                $("#bizTypeConf").find("input:text,input:hidden,textarea").filter("[name]:enabled").each(function(){
                    bizTypeConf[this.name] = this.value;
                })
                editor1.sync();//先同步，才能拿到值
                var content1=$("#content1").val();
                bizTypeConf["description"] = editor1.html();

                saveBizTypeConf(bizTypeConf,function(){
                    alert("保存成功");
                    HAF.closeWindow();
                });
            }
            },
            {label:"退出",clsName:"btn-warning",handle:function(){
                HAF.closeWindow();
            }
            }
        ]
    });

});



//保存信息
function saveBizTypeConf(data,callback){
    HAF.ajax({
        url: HAF.basePath() + "/oa/service/conf/saveOrUpdate.do",
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
