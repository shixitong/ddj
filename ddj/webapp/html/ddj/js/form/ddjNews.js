
//初始化信息
$(function (){
    HAF.Form.init({
        defButtons: false,
        buttons:[
            {label:"保存并发布",clsName:"btn-success",handle:function(){
                var ddjNews = {};
                ddjNews.id=app_id;
                editor1.sync();//先同步，才能拿到值
                ddjNews["strDetail"] =encodeURI(editor1.html());

                saveDdjNews(ddjNews,function(){
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
