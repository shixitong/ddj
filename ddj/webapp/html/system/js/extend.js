$.extend($.fn.validatebox.defaults.rules, {
    number: {
        validator: function (value) {
            return /^-?(?:\d+|\d{1,3}(?:,\d{3})+)(?:\.\d+)?$/.test(value);
        },
        message: '只能输入数字.'
    },
    minLength: {
        validator: function (value, param) {
            return value.length >= param[0];
        },
        message: 'Please enter at least {0} characters.'
    },
    maxLength: {
        validator: function(value, param){
            return value.length <= param[0];
        },
        message: '内容不能多于{0}个字符'
    },
    eqPassword: {
        validator: function (value, param) {
            return value == $(param[0]).val();
        },
        message: '密码不一致！'
    },
    equalTo: {
        isNull: true,	//表示即便没有输入值，也要执行验证
        validator: function (value, param) {
            return value == $(document.getElementById(param[0])).val();
        },
        message: "两次输入的字符不一致"
    },
    mobile: {
        validator: function (value) {
            var regex = /^(1[3458]{1}[0-9]{1}[\d]{8})$/;
            return regex.test(value);
        },
        message: "请输入有效的手机号码"
    },
    email: {
        validator: function (value) {
            return /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i.test(value);
        },
        message: "请输入有效的电子邮件地址"
    },
    url: {
        validator: function (value) {
            return /^(https?|ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(\#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(value);
        },
        message: "请输入有效的URL地址"
    },
    length: {
        validator: function (value, param) {
            var len = jQuery.trim(value).length;
            return len >= param[0] && len <= param[1];
        },
        message: "输入内容长度必须介于{0}和{1}之间"
    },
    /**
     * 营业执照证号验证
     */
    busCert: {
        validator: function (value, param) {
            var len = jQuery.trim(value).length;
            return /^\d*$/.test(value) && (len == param[0] || len == param[1]);
        },
        message: "输入内容必须为长度是{0}或者{1}位的数字"
    },
    /**
     * 税务登记证证号
     */
    taxCert: {
        validator: function (value, param) {
            return /^[A-Z0-9]{15}$/.test(value);
        },
        message: "输入内容必须为长度为15位的数字或者大写字母"
    },
    regex: {
        validator: function (value, param) {
            var reg = new RegExp(param[0], "g");
            return 1 < param.length ? {b: reg.test(value), msg: param[1]} : reg.test(value);
        },
        message: "请输入有效的数据"
    },
    /**
     * 默认可以是小数或整数
     * e.g:
     *     <input type="text" name="obj.name" class="checkbox" validType="decimals[2]" required="true" maxlength="24"/>
     *     表示只能是2位小数或整数
     *
     */
    decimals: {
        validator: function (value, param) {
            var fex = "+";
            if (param && param.length > 0)
                fex = "{" + param[0] + "}";
            var reg = new RegExp("^[+-]?((\\d+\\.\\d" + fex + ")|\\d*)$", "g");
            return reg.test(value);
        },
        message: "请输入有效数字"
    },
    postcode: {
        validator: function (value, param) {
            return /^\d{6}$/.test(value);
        },
        message: '请输入正确的6位邮政编码'
    }
});

//树形结构
$.fn.tree.defaults.loadFilter = function (data, parent) {
    var opt = $(this).data().tree.options;
    var idFiled, textFiled, parentField;
    if (opt.parentField) {
        idFiled = opt.idFiled || 'id';
        textFiled = opt.textFiled || 'text';
        parentField = opt.parentField;
        var i, l, treeData = [], tmpMap = [];
        for (i = 0, l = data.length; i < l; i++) {
            tmpMap[data[i][idFiled]] = data[i];
        }
        for (i = 0, l = data.length; i < l; i++) {
            if (tmpMap[data[i][parentField]] && data[i][idFiled] != data[i][parentField]) {
                if (!tmpMap[data[i][parentField]]['children'])
                    tmpMap[data[i][parentField]]['children'] = [];
                data[i]['text'] = data[i][textFiled];
                tmpMap[data[i][parentField]]['children'].push(data[i]);
            } else {
                data[i]['text'] = data[i][textFiled];
                treeData.push(data[i]);
            }
        }
        return treeData;
    }
    return data;
};

/**
 * 防止panel/window/dialog组件超出浏览器边界
 * @param left
 * @param top
 */
var easyuiPanelOnMove = function (left, top) {
    var l = left;
    var t = top;
    if (l < 1) {
        l = 1;
    }
    if (t < 1) {
        t = 1;
    }
    var width = parseInt($(this).parent().css('width')) + 14;
    var height = parseInt($(this).parent().css('height')) + 14;
    var right = l + width;
    var buttom = t + height;
    var browserWidth = $(document).width();
    var browserHeight = $(document).height();
    if (right > browserWidth) {
        l = browserWidth - width;
    }
    if (buttom > browserHeight) {
        t = browserHeight - height;
    }
    $(this).parent().css({/* 修正面板位置 */
        left: l,
        top: t
    });
};
$.fn.dialog.defaults.onMove = easyuiPanelOnMove;
$.fn.window.defaults.onMove = easyuiPanelOnMove;
$.fn.panel.defaults.onMove = easyuiPanelOnMove;

/**
 *
 * panel关闭时回收内存
 */
$.fn.panel.defaults.onBeforeDestroy = function () {
    var frame = $('iframe', this);
    try {
        if (frame.length > 0) {
            frame[0].contentWindow.document.write('');
            frame[0].contentWindow.close();
            frame.remove();
            if ($.browser.msie) {
                CollectGarbage();
            }
        }
    } catch (e) {
    }
};

/**
 * 描述：jQuery插件，自动在容器中创建背景遮罩及进度条
 * 参数：boolean类型    显示或销毁背景遮罩及进度条        默认“true”——显示
 *         string类型    进度条中显示的文本，支持HTML        默认“Loading...”
 *         object: style = { backStyle:css,frontTyle:css}  前后层样式
 * 用法：$(document.body).bgshade("导入中...")        在body中显示，进度条中显示“导入中...”
 *         $(document.body).bgshade(false);            隐藏body中的背景遮罩和进度条
 *         $(document.body).bgshade({backStyle:{opacity:1}}); 不透明样式
 */
/*$.fn.bgshade = function () {
    var _t = "loading...";
    var _b = true;
    var _o = null;
    //从隐藏参数中读取参数并按类型进行匹配
    for (var i = 0; i < arguments.length && i < 3; i++) {
        var obj = arguments[i];
        if (typeof(obj) == "string") {
            _t = obj;
        } else if (typeof(obj) == "boolean") {
            _b = obj;
        } else if (typeof(obj) == "object") {
            _o = obj;
        }
    }
    var getOffset = function () {
        var doc = this;
        if (this.tagName == 'BODY')
            doc = document[document.compatMode == "CSS1Compat" ? 'documentElement' : 'body'];
        var wi = Math.max(doc.clientHeight, doc.scrollWidth);
        var hi = Math.max(doc.clientHeight, doc.scrollHeight);
        var ti = this.offsetTop;
        var li = this.offsetLeft;
        return {
            backStyle: {
                width: wi,
                height: hi,
                top: ti,
                left: li
            },
            frontStyle: {
                top: ti + hi / 2,
                left: li + wi / 2
            }
        };
    }
    var getStyle = function () {
        return $.extend(true, {
            backStyle: {
                position: "absolute", top: "0px", left: "0px", "z-index": 99999, width: "100%", height: "100%", background: "#777777", opacity: 0.5
            },
            frontStyle: {
                "font-size": "9pt", position: "absolute", top: "50%", left: "50%", "margin-top": "-10px",
                "margin-left": "-75px", "z-index": 199999, width: "150px", "min-height": "22px", "vertical-align": "middle",
                "line-height": "20px", "text-align": "center", color: "#666666",
                "background": "url(" + CONTEXT_PATH + "/html/system/images/loading.gif) no-repeat",
                "background-position": "center"
                //,"background-color":"#fff","border":"2px solid #767676"
            }
        }, _o, getOffset.call(this));
    }
    var destroy = function () {
        //销毁背景遮罩及进度条
        var random = $(this).attr('bgShadeRandom');
        var backId = 'bgshade_b_' + random;
        var frontId = 'bgshade_f_' + random;
        $(this).find('#' + frontId + ',#' + backId).animate({opacity: 'hide'}, function () {
            $(this).remove();
        });
    }
    $(this).each(function () {
        destroy.call(this);
        if (_b) {
            //显示背景遮罩及进度条
            var random = Math.floor(Math.random() * 10000 + 1);
            var backId = 'bgshade_b_' + random;
            var frontId = 'bgshade_f_' + random;
            var style = getStyle.call(this);
            $(this).attr('bgShadeRandom', random);
            $(this).append('<div id="' + backId + '">&nbsp;</div><div id="' + frontId + '">' + _t + '</div>');
            $(this).find('#' + backId).css(style.backStyle);
            $(this).find('#' + frontId).css(style.frontStyle);
        }
    });
}*/

/****
 *检查用户是否具有按钮权限
 */
function checkButtons(scope) {
    var uResource = parent.userResources;
    if (uResource != undefined && uResource != null) {
        var scope = scope || $(document);
        $(".security-button", scope).each(function () {
            if (uResource[$(this).attr("resourceUrl")] == undefined) {
                $(this).hide();
            } else {
                $(this).show();
            }
        });
    }
}
/**
 * 判断是否是用户的资源
 * @param _res ： 资源标识（url或者id），或者资源标识前置字符串（与name配合："/user/"）
 * @param _name ： 资源具体名称（与res连接到一块形成资源标识："save"、"update"）
 * @return {Boolean}
 */
function isUserResource(_res, _name) {
    var _resources = parent ? parent.userResources : userResources;
    if (_resources != undefined && _resources != null) {
        var _resource = _name ? (_res + _name) : _res;
        if (_resources[_resource] == undefined) {
            return false;
        } else {
            return true;
        }
    } else {
        alert("系统错误：未获取的用户资源权限信息！");
        return false;
    }
}

/*$.fn.loadmask = function () {
    var _t = "加载中...";
    var _b = true;
    var _o = null;
    //从隐藏参数中读取参数并按类型进行匹配
    for (var i = 0; i < arguments.length && i < 3; i++) {
        var obj = arguments[i];
        if (typeof(obj) == "string") {
            _t = obj;
        } else if (typeof(obj) == "boolean") {
            _b = obj;
        } else if (typeof(obj) == "object") {
            _o = obj;
        }
    }
    _o = $.extend({"bgcolor": "#ffffff", "opacity": 1, "txtbgcolor": "#ffffff", "txtcolor": "#666666"}, _o);
    //
    var _getStyle = function () {
        var doc = this;
        if (this.tagName == 'BODY') doc = document[document.compatMode == "CSS1Compat" ? "documentElement" : "body"];
        var wi = Math.max(doc.clientHeight, doc.scrollWidth);
        var hi = Math.max(doc.clientHeight, doc.scrollHeight);
        var ti = this.offsetTop;
        var li = this.offsetLeft;
        return {
            backStyle: {
                "position": "absolute",
                "z-index": 99999,
                "top": ti,
                "left": li,
                "width": wi,
                "height": hi,
                "background-color": _o.bgcolor,
                "opacity": _o.opacity
            },
            frontStyle: {
                "font-size": "9pt",
                "position": "absolute",
                "z-index": 100000,
                "top": ti + hi / 2 - 15,
                "left": li + wi / 2 - 35,
                "width": "auto",
                "min-height": "20px",
                "vertical-align": "middle",
                "line-height": "20px",
                "text-align": "center",
                "color": _o.txtcolor,
                "background": "url(" + CONTEXT_PATH + "/html/frame/css/images/loading.gif) no-repeat left center",
                "background-color": _o.txtbgcolor,
                "padding": "1px 1px 1px 18px"
            }
        };
    }
    var _destroy = function () {
        //销毁背景遮罩及进度条
        var random = $(this).attr('bgShadeRandom');
        var backId = 'bgshade_b_' + random;
        var frontId = 'bgshade_f_' + random;
        $(this).find('#' + frontId + ',#' + backId).animate({opacity: 'hide'}, function () {
            $(this).remove();
        });
    }
    $(this).each(function () {
        _destroy.call(this);
        if (_b) {
            //显示背景遮罩及进度条
            var random = Math.floor(Math.random() * 10000 + 1);
            var backId = 'bgshade_b_' + random;
            var frontId = 'bgshade_f_' + random;
            var style = _getStyle.call(this);
            $(this).attr('bgShadeRandom', random);
            $(this).append('<div id="' + backId + '">&nbsp;</div><div id="' + frontId + '">' + _t + '</div>');
            $(this).find('#' + backId).css(style.backStyle);
            $(this).find('#' + frontId).css(style.frontStyle);
        }
    });
}*/

/***
 * 以XXXX结尾
 * @param s
 * @return {boolean}
 */
String.prototype.endWith = function (s) {
    if (s == null || s == "" || this.length == 0 || s.length > this.length)
        return false;
    if (this.substring(this.length - s.length) == s)
        return true;
    else
        return false;
    return true;
}

/***
 * 以XXXX开始
 * @param s
 * @return {boolean}
 */
String.prototype.startWith = function (s) {
    if (s == null || s == "" || this.length == 0 || s.length > this.length)
        return false;
    if (this.substr(0, s.length) == s)
        return true;
    else
        return false;
    return true;
}


$.extend($.fn.datagrid.defaults.editors, {
    treeDialog: {//自定义editor的名称
        init: function (container, options) {
            var input = $('<input class="datagrid-editable-input"/>').appendTo(container);
            return input;
        },
        getValue: function (target) {
            return $(target).val();
        },
        setValue: function (target, value) {
            alert(value);
            $(target).val(value);
        },
        resize: function (target, width) {
            var input = $(target);
            if ($.boxModel == true) {
                input.width(width - (input.outerWidth() - input.width()));
            } else {
                input.width(width);
            }
        }
    },

    selecttypebox: {
        init: function (container, options) {
            var selectStr = '<select name="sysNumber.type">' +
                '<option value="1">系统预制</option>' +
                '<option value="0">系统自建</option>' +
                '</select>';
            var select = $('\"' + selectStr + '\"').appendTo(container);
            return select;
        },
        getValue: function (target) {
            return $(target).val();
        },
        setValue: function (target, value) {
            $(target).val(value);
        },
        resize: function (target, width) {
            var select = $(target);
            if ($.boxModel == true) {
                select.width(width - (select.outerWidth() - select.width()));
            } else {
                select.width(width);
            }
        }
    }

});


/***
 * js 实现trim函数
 * @return {String}
 * @constructor
 */
String.prototype.Trim = function()
{
    return this.replace(/(^\s*)|(\s*$)/g, "");
}
String.prototype.LTrim = function()
{
    return this.replace(/(^\s*)/g, "");
}
String.prototype.RTrim = function()
{
    return this.replace(/(\s*$)/g, "");
}


/***
 *
 * @param obj
 * @return {*}
 */
var getString = function (obj){
    if(obj == null || typeof(obj) == "undefined"){
        return "";
    }else{
        return obj.toString();
    }
}

$.topWindow = function(opts,callback){
    var p = window.top;
    if(!p) p = window;
    p.$(p.document.body).append("<div></div>");
    var e = p.$(p.document.body).find("div:last");
    var _opts = $.extend({
        onOpen: function(){
            e.window("body").css({overflow:"hidden"});
            if(e.window("options").top<0) e.window("move",{top:0});
            if(e.window("options").left<0) e.window("move",{left:0});
        },
        onClose: function(){
            e.window("destroy");
            if(typeof callback == "function"){
                callback();
            }
        },
        style: {"textAlign":"left"},
        collapsible: false,
        minimizable: false,
        maximizable: false,
        iconCls:'icon-save',
        width: 400,
        height: 200,
        modal: true,
        shadow: true
    },opts);
    if(_opts.href){
        _opts.content = "<iframe frameborder='0' border='0' width='100%' height='100%' src='"+opts.href+"'></iframe>";
        _opts.href = null;
    }
    e.window(_opts,p);
};