/**
 * HAF 工具库
 * 14. getRandomNum(numLength)  //获取满足长度的随机数字字符串：89123,10902，……

 * 16. footPopover(options)     //页脚通知小弹窗：类似QQ消息框，从页面右下角浮出
 */


/***************
 * 获取满足长度的随机数字字符串：89123,10902，……
 * @param numLength
 */
function _getRandomNum(numLength){
    var multiplier = Math.pow(10, numLength);
    return ((1 + Math.random()) * multiplier + "").substr(1, numLength);
}


/**
 * 方法扩展到HAF工具类
 */
$.extend(window.HAF, {
    getRandomNum: function(numLength){
        return _getRandomNum(numLength);
    }

});



HAF.footPopoverId = "";
HAF.footPopover = function(arg1,arg2){
    var options = {
        width: 200,
        height: 150,
        title: "通知",
        content: "通知内容",
        style: "primary",   //bootstrap panel styles: primary\default\info\...
        href: "",
        mode: "ajaxLoad",	  //"iframe","ajaxLoad"：当定义href是生效
        ready: false,		  //dom ready function
        timeInterval: 10,   //自动展现或者隐藏时上浮或者下沉单位高度间隔时间
        showTimeOut: 1000,  //完全展现后停留时间
        hideTimeOut: 0      //完全隐藏后停留时间（0：不再显示）
    };
    if(typeof arg1=="object"){
        $.extend(options, arg1);
    }else if(arg2){
        options[arg1] = arg2;
    }

    var popoverId = HAF.footPopoverId;
    if(!popoverId){
        popoverId = "fPop" + HAF.getRandomNum(5) + HAF.getRandomNum(5);
        HAF.footPopoverId = popoverId;
    }

    var $popover = $('#' + popoverId);
    if($popover.size()==0){
        $popover = $('<div id="' + popoverId + '" class="haf-foot-popover" style="bottom:-1000px"></div>');
        $popover.appendTo('body');
        $popover.append('<div class="panel panel-default">' +
            '<div class="panel-heading">' +
            '<span></span>' +
            '<button type="button" class="close"><span>×</span></button>' +
            '</div>' +
            '<div class="panel-body"></div>' +
            '</div>');
    }else{
        $popover.css({bottom: "-1000px"});
    }

    $popover.find("> div.panel").removeClass().addClass("panel panel-"+options.style);

    var $title = $popover.find("> .panel > .panel-heading > span");
    var $body = $popover.find("> .panel > .panel-body");
    $title.empty().append(options.title);
    $body.empty();
    $body.height(options.height);
    $body.width(options.width);
    if(options.href){
        if(options.mode=="iframe"){
            var $iframe = $('<iframe src="' + options.href + '" frameborder="no"></iframe>');
            $iframe.load(function(){
                if(options.ready) options.ready($popover,$title,$body);
            });
            $iframe.appendTo($body);
        }else{
            $body.load(options.href, function(){
                if(options.ready) options.ready($popover,$title,$body);
            });
        }
    }else{
        $body.append(options.content);
        if(options.ready) options.ready($popover,$title,$body);
    }

    $popover.width($body.outerWidth());

    var minBottom = $popover.height()*-1 - 2;
    $popover.css({bottom: minBottom});

    var bottom = minBottom;

    var timer1 = 0;
    var timer2 = 0;
    var timeInterval = options.timeInterval || 10;
    var showTimeOut = options.showTimeOut || 1000;

    function _up(){
        timer1 = window.setInterval(function(){
            //var ri = $(window).height() - $popover.offset().top - $popover.height();
            if(timer2){
                window.clearInterval(timer2);
            }
            if(bottom<0){
                bottom += 1;
                $popover.css("bottom", bottom);
            }else{
                if(timer1){
                    window.clearInterval(timer1);
                    timer1 = 0;
                    setTimeout(function(){
                        if(!timer2) _down();
                    }, showTimeOut);
                }
            }
        }, timeInterval);
    }

    function _down(){
        if(timer1){
            window.clearInterval(timer1);
            timer1 = 0;
        }
        timer2 = window.setInterval(function(){
            if(bottom>minBottom){
                bottom -= 1;
                $popover.css("bottom", bottom);
            }else{
                if(timer2){
                    window.clearInterval(timer2);
                    timer2 = 0;
                    if(options.hideTimeOut){
                        setTimeout(function(){
                            _up();
                        }, options.hideTimeOut);
                    }
                }
            }
        }, timeInterval);
    }

    //关闭窗口
    $popover.find("> .panel > .panel-heading > .close").click(function(){
        _down();
    });

    //浮出窗口
    _up();
}