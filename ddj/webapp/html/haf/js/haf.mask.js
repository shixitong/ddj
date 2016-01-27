/**
 * Created by zhangjunhui on 13-12-20.
 * body mask
 */
(function(window, undefined, $){

    if(!window.HAF){
        window.HAF = {};
    }

    $.extend(window.HAF,{
        //页面遮罩================================
        showMask: function(){

            var args = [true,false,false,""]; //是否open，是否显示msg，是否点击关闭，消息内容
            var ai = 0;
            for(var i=0;i<arguments.length;i++){
                if(typeof arguments[i] == "boolean") args[ai++] = arguments[i];
                else args[3] = arguments[i];
            }
            var isShow = args[0], withMsg = args[1], canClick = args[2], msg = args[3];
            if(args[3]) withMsg = true;

            var $mask = $(".haf-body-mask");
            var $body = $(document.body);

            if(!isShow){
                if($mask.size()>0){
                    $mask.hide();
                    $body.removeClass("haf-mask-show");
                }
                return;
            }

            $body.addClass("haf-mask-show");

            if($mask.size()==0){
                $mask = $("<div class='haf-body-mask'><div class='haf-body-mask-front'></div><div class='haf-body-mask-back'></div></div>");
                $body.append($mask);
            }

            if(withMsg){
                $mask.find(".haf-body-mask-front").html("<div class='haf-mask-msg'><div class='haf-mask-msg-inner'>"+(msg||"")+"</div></div>");
            }else{
                $mask.find(".haf--mask-msg").remove();
            }

            if(canClick){
                $mask.unbind("click").click(function(){
                    HAF.showMask(false);
                });
            }

            $mask.show();
        }
    });

})(window, undefined, jQuery);
