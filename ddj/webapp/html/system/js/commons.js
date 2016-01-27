(function($){
	/**
	 * 重写jquery的ajax方法
	 * 解决异步请求异常时给予默认处理方法
	 */
	var ajax_org=$.ajax;
	var error = function(xhr,status){
		var errHeader = "Error-Permit-Json";
		var str = xhr.getResponseHeader(errHeader);
		if(str){
			str = decodeURIComponent(str);
			top.jQuery.messager.alert('错误',str,'error');
			//var errmsg = JSON.parse(xhr.getResponseHeader(errHeader));
			//alert(xhr.getResponseHeader(errHeader));
		}
	}
	$.ajax=function(s){
		var error_org=s.error;
		s.error=function(xhr,status,err){
			error(xhr,status);
			if(error_org)error_org(xhr,status,err);
		};
		
		/**
		 * 如果url地址上有mid或permitCode，则自动为ajax请求的url附件参数
		 */
		var mid = request("mid");
		if(mid!=''&&0>s.url.indexOf("mid=")){
			s.url = s.url+(s.url.indexOf("?")<0?"?":"&")+"mid="+mid;
		}
		var permit = request("permitCode");
		if(permit!=""&&0>s.url.indexOf("permitCode=")){
			s.url = s.url+(s.url.indexOf("?")<0?"?":"&")+"permitCode="+permit;
		}
		s.url = CONTEXT_PATH + s.url;
		return ajax_org(s);
	}
	
	/**
	 * 该js重写原jQuery的click(fn)事件，如果有对象是linkbutton则重写。
	 * 编写原因：easyui的linkbutton插件disabled时不支持jQuery的click(fn)绑定方式，支持在标签上配置herf或onclick
	 */
	var getEvent = function(data){
		return function(e){
			if(!$(this).linkbutton('options').disabled) data.call(this, e);
		}
	}
	var org_click = jQuery.fn.click;
	$.fn.click=function(data, fn){
		if($(this).is('.easyui-linkbutton')) data = getEvent(data);
		return arguments.length > 0 ? org_click.call(this, data, fn) : this.trigger("click");
	};
	$.fn.bind=function(types, data, fn) {
		if(types=='click'&&$(this).is('.easyui-linkbutton')) data = getEvent(data);
		return this.on(types, null, data, fn);
	}
	
	/**
	 * 获取选中区域中的表单元素值，返回JSON对象
	 */
	$.fn.getParamJson=function(){
		var json = {};
		var jsonHandle = function(){
			var _el = $(this);
			if(_el.is(':disabled')) return true;
			if(_el.is(':submit,:image,:reset,:button')) return true;
			var name = jQuery.trim(_el.attr('name'));
			if(name == '') name = jQuery.trim(_el.attr('id'));
			if(name == '') return true;
			var value = json[name];
			if(typeof(value)=='undefined'){
				if(_el.is(':radio:checked,:checkbox:checked,:text,:file,:password,textarea,input[type="hidden"]')) json[name] = _el.val();
				if(_el.is('select')){
					if(_el.attr('multiple')=='multiple'){
						json[name] = [];
						_el.find('option:selected').each(function(){
							json[name].push($(this).val());
						});
					}else json[name] = _el.find('option:selected').val();
				}
			}else if(jQuery.isArray(value)){
				if(_el.is(':checkbox:checked,:text,:file,:password,textarea,input[type="hidden"]')) json[name].push(_el.val());
				if(_el.is('select')){
					_el.find('option:selected').each(function(){
						json[name].push($(this).val());
					});
				}
			}else{
				if(_el.is(':checkbox:checked,:text,:file,:password,textarea,input[type="hidden"]')){
					json[name] = [value];
					json[name].push(_el.val());
				}
				if(_el.is('select')){
					json[name] = [value];
					_el.find('option:selected').each(function(){
						json[name].push($(this).val());
					});
				}
			}
		}
		this.each(function() {
			var _this = $(this);
			if(_this.is(':input')) jsonHandle.call(this);
			else _this.find(':radio:checked,:checkbox:checked,:text,:file,:password,select,textarea,input[type="hidden"]').each(jsonHandle);
		});
		return json;
	}
	/**
	 * 获取选中区域中的表单元素值，返回URL字符串
	 */
	$.fn.getParamUrl=function(){
		return jQuery.param(this.getParamJson(), true);
	}
})(jQuery);

/**
 * 获取URL上的参数值
 * @param {} paras
 * @return {String}
 */
function request(paras){ 
	var url = location.href;  
	var paraString = url.substring(url.indexOf("?")+1,url.length).split("&");  
	var paraObj = {}  
	for (i=0; j=paraString[i]; i++){  
		paraObj[j.substring(0,j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=")+1,j.length);  
	}
	var returnValue = paraObj[paras.toLowerCase()];
	if(typeof(returnValue)=="undefined"){
		return "";
	}else{
		return returnValue;
	}
}

function escapeHtmlTag(html){
	html = html.replace(/</ig,"&lt;");
	html = html.replace(/>/ig,"&gt;");
	html = html.replace(/"/ig,"&quot;");
	return html;
}

function addPermit(url){
	var mid = request("mid");
	if(mid!=''&&0>url.indexOf("mid=")){
		url = url+(url.indexOf("?")<0?"?":"&")+"mid="+mid;
	}
	var permit = request("permitCode");
	if(permit!=""&&0>url.indexOf("permitCode=")){
		url = url+(url.indexOf("?")<0?"?":"&")+"permitCode="+permit;
	}
	return url;
}

//对Date的扩展，将 Date 转化为指定格式的String 
//月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
//年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
//例子： 
//(new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
//(new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
Date.prototype.Format = function(fmt) {
	var weeks = ["星期日","星期一","星期二","星期三","星期四","星期五","星期六"];
	var o = {
		"M+" : this.getMonth()+1,					//月份
		"d+" : this.getDate(),						//日
		"[hH]+" : this.getHours(),					//小时
		"m+" : this.getMinutes(),					//分
		"s+" : this.getSeconds(),					//秒
		"q+" : Math.floor((this.getMonth()+3)/3),	//季度
		"S"  : this.getMilliseconds(),				//毫秒
		"E"	 : weeks[this.getDay()]					//周名
	}; 
	if(/(y+)/.test(fmt)) 
		fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
	for(var k in o) 
		if(new RegExp("("+ k +")").test(fmt)) 
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
	return fmt; 
}

/**
 * 将对象转成json字符串
 * @param obj
 * @return
 */
function obj2Str(obj){  
	switch(typeof(obj)){  
	   case 'object':  
	   		var ret = [];  
	    	if(obj instanceof Array){  
		     	for (var i = 0, len = obj.length; i < len; i++){  
		      		ret.push(obj2Str(obj[i]));  
		     	}  
		     	return '[' + ret.join(',') + ']';  
	    	}else if (obj instanceof RegExp){  
		     	return obj.toString();  
		    }else{  
			    for (var a in obj){  
			      	ret.push(a + ':' + obj2Str(obj[a]));  
			    }  
		     	return '{' + ret.join(',') + '}';  
		    }  
	   case 'function':  
	    	return 'function() {}';  
	   case 'number':  
	    	return obj.toString();  
	   case 'string':  
	    	return "\"" + obj.replace(/(\\|\")/g, "\\$1").replace(/\n|\r|\t/g, function(a) {return ("\n"==a)?"\\n":("\r"==a)?"\\r":("\t"==a)?"\\t":"";}) + "\"";  
	   case 'boolean':  
	    	return obj.toString();  
	   default:  
	    	return obj.toString();  
	}  
}

jQuery(document).ready(function(){
	/**
	 * 给输入框添加焦点时全选功能
	 */
	$(':text,:password').focus(function(){
		var _this = $(this);
		_this.select().addClass('focusMark');
		if(_this.is(".promptForInput")){
			_this.removeClass("promptForInput").val("");
		}
	}).blur(function(){
		var _this = $(this);
		if(_this.val()==""&&_this.attr("title")){
			_this.addClass("promptForInput").val(_this.attr("title"));
		}
	}).mouseup(function(e){
		if($(this).is('.focusMark')){
			$(this).removeClass('focusMark')
			e.preventDefault();
		}
	}).each(function(){
		var _this = $(this);
		if(_this.val()!="") return;
		if(_this.attr("title")){
			_this.val(_this.attr("title")).addClass("promptForInput");
		}
	});
	/**
	 * 为每一个form的action属性增加permitCode
	 */
	var permit = request("permitCode");
	if(permit!=""){
		$("form").each(function(){
			var url = $(this).attr("action");
			$(this).attr("action", (CONTEXT_PATH+url+(url.indexOf('?')<0?'?':'&')+"permitCode="+permit));
		});
	}
});


//关闭窗口
function closeWindow(){
    try{
        window.open("","_parent","");
        window.close();
        if(window) window.location = "about:blank";
    }catch(e){
        window.location = "about:blank";
    }
}