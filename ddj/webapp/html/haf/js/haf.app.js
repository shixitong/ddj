//表单初始化操作
$(function(){

    //取消超链接的focus事件，用于去除点击后的虚框
    $(document).delegate("a","focus",function(){this.blur();});

    //将form title元素显示在顶部按钮区域
    var _tit = $(".page-header.pull-top");
    if(_tit.size()>0){
        _tit.clone().appendTo(".flow-form-header");
        var ti = $(".flow-form-header").outerHeight() + 5;
        $(".flow-form-backdrop").css({"top": ti});
        $(".flow-form-content").css({"margin-top": ti});
    }

    //timer function
    var footer_timer = null;
    var setFooterTimer = function(){
        if($(".flow-form-footer .timer").size()>0){
            footer_timer = setInterval(function(){
                var time = new Date();
                var s = time.getFullYear() + "-" + (time.getMonth()+1) + "-" + time.getDate() +
                    " " + time.getHours() + ":" + time.getMinutes() + ":" + time.getSeconds();
                $(".flow-form-footer .timer").html(s);
            },1000);
        }
    }
    var clearFooterTimer = function(){
        if(footer_timer) clearInterval(footer_timer);
    }

    //启动timer
    setFooterTimer();

    //表单页脚工具栏显示与隐藏
    $(".flow-form-footer div a.hide-footer").click(function(){
        $(".flow-form-footer > div").hide();
        $(".flow-form-footer > a.show-footer").show();
        $(".flow-form-backdrop,.flow-form-content").addClass("hide-footer");
        clearFooterTimer();
    });
    $(".flow-form-footer > a.show-footer").click(function(){
        $(".flow-form-footer > a.show-footer").hide();
        $(".flow-form-footer > div").show();
        $(".flow-form-backdrop,.flow-form-content").removeClass("hide-footer");
        setFooterTimer();
    });

    //header，footer scroll with body
    var scrollFixedEl = function(){
        var left = $(window).scrollLeft();
        $(".flow-form-header,.flow-form-footer").css("left",left*-1);
    }
    scrollFixedEl();
    $(window).scroll(function(){scrollFixedEl();});
    $(window).resize(function(){scrollFixedEl();});
});