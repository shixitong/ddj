
var timeOut="";
//初始化信息
$(function (){
    HAF.Form.init({
        defButtons: false,
        buttons:[
            {label:"退出",clsName:"btn-warning",handle:function(){
                HAF.closeWindow();
            }
            }
        ]
    });

    HAF.Form.downList("#stationtypeText",{//（1出租2求助3办公室4免费孵化器工位）
        data:[["1","工位"],["3","办公室"]],
        valueField:"#stationtype",textField:"#stationtypeText"
    })

    HAF.Form.readonly("#_hf_apply_info");

    if(stationtype=="1"){
        $("#office").html("工位信息");
    }else{
        $("#office").html("办公室信息");
    }

    initImages();

});

/**
 * 初始化照片
 */
function initImages(){
    if(photos!=""){
        var imgs=photos.split(",");
        for(var i=0;i<imgs.length;i++){

            var url=serviceUrl+imgs[i];
            var imageDiv=$("#imageDiv");
            var html='<div style="height:160px;width:155px;text-align:center;float:left"><a href="javascript:void(0)"><img onclick="openLink(this)" style="height:150px;width:150px"' +
                ' data-id="'+imgs[i]+'" src="'+url+'" /></a> <button  onclick="deleteImg(this)">删除</button></div>';
            imageDiv.append(html);
        }
    }
}


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
            var url=serviceUrl+idPhoto;
            var imageDiv=$("#imageDiv");
            var html='<div style="height:160px;width:155px;text-align:center;float:left"><a href="javascript:void(0)"><img onclick="openLink(this)" style="height:150px;width:150px"' +
                ' data-id="'+idPhoto+'" src="'+url+'" /></a> <button  onclick="deleteImg(this)">删除</button></div>';
            imageDiv.append(html);


            var newImgs="";
            $("#imageDiv").find("img").each(function(){
                    newImgs+=$(this).data("id")+",";
            });
            newImgs=newImgs.substr(0,newImgs.length-1);
            var data={};
            data.id=appId;
            data.photos=newImgs;
            saveImgData(data,function(){})
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
 * 删除图片
 */
function deleteImg(key){
     var newImgs="";

      var img=$(key).parent().find("img");
      var delImg=$(img).data("id");
      $("#imageDiv").find("img").each(function(){
          if($(this).data("id")!=delImg){
              newImgs+=$(this).data("id")+",";
          }else{
              $(key).parent().remove();
          }
      });

    if(newImgs!=""){
        newImgs=newImgs.substr(0,newImgs.length-1);
    }

    var data={};
    data.id=appId;
    data.photo=delImg;
    data.photos=newImgs;
    deleteImgData(data,function(){})
}


//删除图片
function deleteImgData(data,callback){
    HAF.ajax({
        url: HAF.basePath() + "/stationinfo/deleteImg.do",
       // contentType : 'application/json',
        data: data,
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

/*
保存图片
 */
function saveImgData(data,callback){
    HAF.ajax({
        url: HAF.basePath() + "/stationinfo/saveImg.do",
        // contentType : 'application/json',
        data: data,
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



