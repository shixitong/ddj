HAF.Form.setValidate({
    els:{
        "company": {required:true},
        "stationtypeText": {required:true},
        "area": {required:true},
        "street": {required:true},
        "address": {required:true},
        "square": {required:true,type:'number'},
        "price": {required:true,type:'number'},
        "communicate": {required:true}
    }
});


var timeOut="";
var photos=[];
//初始化信息
$(function (){
    HAF.Form.init({
        defButtons: false,
        buttons:[
            {label:"保存",clsName:"btn-success",handle:function(){
                var activities = {};
                $("#activities").find("input:text,input:hidden,textarea").filter("[name]:enabled").each(function(){
                    activities[this.name] = this.value;
                })


      /*          HAF.ajax({
                    url: HAF.basePath() + "/static/saveOrUpdate.do",
                    contentType : 'application/json',
                    data:HAF.stringify(activities),
                    success:function(result){
                    if(result=="200"){
                        alert("保存成功！");
                    }else{
                        alert(result.msg);
                    }
                }
            })*/
               // activities.photos2=photos;
                if(HAF.Form.validate()){
                    saveStationinfo(activities,function(){
                        alert("成功");
                        window.opener.$stationinfoGrid.datagrid("reload");
                        HAF.closeWindow();
                    });
                }

            }
            },
            {label:"退出",clsName:"btn-warning",handle:function(){
                HAF.closeWindow();
            }
            }
        ]
    }).datepicker().downList().personSelect().comorgSelect().uploadify();

    HAF.Form.downList("#stationtypeText",{//（1出租2求助3办公室4免费孵化器工位）
        data:[["1","工位"],["3","办公室"]],
        valueField:"#stationtype",textField:"#stationtypeText",
        onSelect:function(val,text){
            if(val=="1"){
                $("#office").html("工位信息");
            }else{
                $("#office").html("办公室信息");
            }
        }
    })

    $("#address").keyup(function(){
        var val=$(this).val();
       clearTimeout(timeOut);
        if(val!=""){
            timeOut=setTimeout(function(){
                searchAddress(val);
            },500);
        }
    });
});


/**
 * 上传附件
 */
function uploadAttachment(){

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

            $("#photos").val($("#photos").val()+","+idPhoto);
          //  photos.push(idPhoto);

            var url="http://115.28.25.120:19083/img?icon=0&photourl="+idPhoto;
            var imageDiv=$("#imageDiv");
            var html='<a href="#"><img onclick="openLink(this)" style="height:150px;width:150px" data-id="'+idPhoto+'" src="'+url+'" /></a>';
            imageDiv.append(html);

        }
    });
}
function openLink(key){
    window.open($(key).attr("src"));
}


//保存信息
function saveActivities(data,callback){
    $.ajax({
        url: HAF.basePath() + "/static/saveOrUpdate.do",
        //contentType : 'application/json',
        data:data,
        type:"post",
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


//保存信息
function saveStationinfo(data,callback){
    HAF.ajax({
        url: HAF.basePath() + "/stationinfo/saveOrUpdate.do",
        contentType : 'application/json',
        data: HAF.stringify(data),
        success: function(rdata){
            if(rdata.id){
                $.extend(data, rdata);
                if(callback) callback(data);
            }else{
                alert("操作失败，未保存成功");
            }
        }
    });
}


//保存信息
function saveStationinfo2(data,callback){
    HAF.ajax({
        url: HAF.basePath() + "/static/saveOrUpdate.do",
    //    contentType : 'application/json',
        type:"post",
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
            HAF.Form.downList("#address",{//（1出租2求助3办公室4免费孵化器工位）
                editable:true,
                data:textValue,
               textField:"#address",
               onSelect:function(val,text){
                   if(val!=""){
                       var location=val.split(",");
                       if(location.length>1){
                           $("#latitude").val(location[0]);
                           $("#longitude").val(location[1]);
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


