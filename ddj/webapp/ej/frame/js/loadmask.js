$(document.body).loadmask();
$.parser.onComplete = function(){
    $(document.body).loadmask(false);
};