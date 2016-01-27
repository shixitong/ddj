/**
 * HAF 工具库
 * 1. dataType
 * 2. ajax
 * 3. stringify : object to string
 * 4. urlSerialize: json data to serialize
 */
(function(window, undefined, $){

    if(!window.CONTEXT_PATH){
        window.CONTEXT_PATH = "/haf";
    }

    if(!window.HAF){
        window.HAF = {};
    }

    var _basePath = CONTEXT_PATH;

    //获取对象类型
    function _typeOf(o){
        var dataType =  typeof(o)=="object" ? Object.prototype.toString.call(o) : typeof(o);
        switch(dataType){
            case "[object Array]":  return "array";
            case "[object Object]":  return "object";
            case "[object Date]":  return "date";
            default: return dataType;
        }
    }

    //对象转换为格式化json字符串
    function _toString(o){
        var arr = [];
        var isArr = _typeOf(o)=="array";
        var fmt = function(s) {
            if (typeof s == "object" && s != null) return _toString(s);
            return /^(string)$/.test(typeof s) ? '"' + s.replace(/\\/g,"\\\\").replace(/\n/g,"\\n").replace(/\r/g,"\\r").replace(/\"/g,'\\\"') + '"' : s;
        };
        switch(_typeOf(o)){
            case "array": isArr=true; break;
            case "object": isArr=false; break;
            case "date": return fmt(o.toString());
            case "function": return "'function'"; //(/^[\s\(]*function(?:\s+([\w$_][\w\d$_]*))?\(/).exec(o.toString())[1] || '';
            default: return fmt(o);
        }
        for (var i in o){
            if(isArr) arr.push(fmt(o[i]));
            else arr.push('"' + i + '":' + fmt(o[i]));
        };
        return isArr ? ( '[' + arr.join(',') + ']' ) : ( '{' + arr.join(',') + '}' );
    }

    //url参数序列化：json序列化为&k=v&k1=v1格式，仅支持向下嵌套一层{k:{k:v},k:[v,v],k:v}
    function _urlSerialize(json){
        var ss = "";
        $.each(json, function(k, v){
            if(_typeOf(v)=="object"){
                $.each(v, function(k1, v1){
                    ss += "&" + k + "." + k1 + "=" + encodeURIComponent(v1);
                });
            }else if(_typeOf(v)=="array"){
                $.each(v, function(i, v1){
                    ss += "&" + k + "=" + encodeURIComponent(v1);
                    //ss += "&" + k + "[" + i + "]=" + encodeURIComponent(v1);
                });
            }else{
                ss += "&" + k + "=" + encodeURIComponent(v);
            }
        });
        return ss.replace(/^&/,"");
    }

    // $.ajax ,常用参数封装
    var _ajax_num = 0;  //记录同时执行的_ajax次数

    $.ajaxSetup({
        error: function(xhr,sta,err){
            if(xhr.status==200 && xhr.responseText){
                alert(xhr.responseText);
            }else{
                alert("系统错误：" + sta + " - " + err);
            }
        }
    });

    function _ajax(options){
        var options = $.extend({
            type: "POST",
            async: "true",
            dataType: "json",
            beforeSend: function(){
                if(_ajax_num==0) HAF.showMask("请稍后...");
                _ajax_num ++;
            },
            complete: function(){
                _ajax_num --;
                if(_ajax_num==0) HAF.showMask(false);
            }
        }, options);
        $.ajax(options);
    }

    //关闭窗口
    function _closeWindow(){
        try{
            window.open("","_parent","");
            window.close();
            if(window) window.location = "about:blank";
        }catch(e){
            window.location = "about:blank";
        }
    }

    /** getKeyValue, setKeyValue
     * getKeyValue ： json对象或者json集合操作：返回value或者value数组或者连接后的字符串
     * setKeyValue ： json对象键值对设置，允许多值传递
     */
    function _getKeyValue(obj, key, joinStr){
        if(!obj) return "";
        if(_typeOf(obj)=="array"){
            var r = [];
            $.each(obj, function(i,dd){ r.push(typeof dd[key]=="undefined" ? "" : dd[key]); });
            if(joinStr){return r.join(joinStr);}else{return r;}
        }else{
            return typeof obj[key]=="undefined" ? "" : obj[key];
        }
    }
    function _setKeyValue(o, k, v){
        if(typeof k=="object"){
            $.extend(o, k);
        }else{
            o[k] = v;
        }
    }

    /**
     * 方法扩展到HAF工具类
     */
    $.extend(window.HAF, {

        basePath: function(s){
            if(arguments.length>0){
                _basePath = s;
            }else{
                return _basePath;
            }
        },

        //数据转string：json、array、date、string、number
        stringify: function(o){
            return _toString(o);
        },

        //数据类型判断
        dataType: function(o){
            return _typeOf(o);
        },

        //URL Query Param Serialize
        urlSerialize: function(o){
            return _urlSerialize(o);
        },

        //强制关闭窗口
        closeWindow: function(){
            _closeWindow();
        },

        //jquery ajax 封装
        ajax: function(options){
            _ajax(options);
        },

        //json值获取
        getKeyValue: function(o, k, s){
            return _getKeyValue(o, k, s);
        },
        //json值设置
        setKeyValue: function(o, k, v){
            _setKeyValue(o, k, v);
        }
    });

})(window, undefined, jQuery);