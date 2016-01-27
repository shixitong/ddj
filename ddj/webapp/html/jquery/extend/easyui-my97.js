/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-5-2
 * Time: 下午2:59
 * easyui中使用my97时间控件
 */

(function ($) {
    $.fn.my97 = function (options, params) {
        if (typeof options == "string") {
            return $.fn.my97.methods[options](this, params);
        }
        options = options || {};
        if (!WdatePicker) {
            alert("未引入My97js包！");
            return;
        }
        return this.each(function () {
            var data = $.data(this, "my97");
            var newOptions;
            if (data) {
                newOptions = $.extend(data.options, options);
                data.opts = newOptions;
            } else {
                newOptions = $.extend({}, $.fn.my97.defaults, $.fn.my97.parseOptions(this), options);
                $.data(this, "my97", {
                    options : newOptions
                });
            }
            $(this).addClass('Wdate').click(function () {
                WdatePicker(newOptions);
            });
        });
    };
    $.fn.my97.methods = {
        setValue : function (target, params) {
            target.val(params);
        },
        getValue : function (target) {
            return target.val();
        },
        clearValue : function (target) {
            target.val('');
        }
    };
    $.fn.my97.parseOptions = function (target) {
        return $.extend({}, $.parser.parseOptions(target, ["el", "vel", "weekMethod", "lang", "skin", "dateFmt", "realDateFmt", "realTimeFmt", "realFullFmt", "minDate", "maxDate", "startDate", {
            doubleCalendar : "boolean",
            enableKeyboard : "boolean",
            enableInputMask : "boolean",
            autoUpdateOnChanged : "boolean",
            firstDayOfWeek : "number",
            isShowWeek : "boolean",
            highLineWeekDay : "boolean",
            isShowClear : "boolean",
            isShowToday : "boolean",
            isShowOthers : "boolean",
            readOnly : "boolean",
            errDealMode : "boolean",
            autoPickDate : "boolean",
            qsEnabled : "boolean",
            autoShowQS : "boolean",
            opposite : "boolean"
        }
        ]));
    };
    $.fn.my97.defaults = {
        dateFmt : 'yyyy-MM-dd HH:mm:ss'
    };

    $.parser.plugins.push('my97');
})(jQuery);
