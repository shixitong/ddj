(function($){
	var jQuery = $;
	
	/**
	 * 创建按钮html
	 */
	function createBtnHtml(btn){
		return "<a id=\""+btn.id+"\" iconCls=\""+btn.iconCls+"\" title=\""+escapeHtmlTag(btn.desc)+"\" href=\"javascript:void(0)\" class=\"easyui-linkbutton\" plain=\"true\">"+
			escapeHtmlTag(btn.name)+"</a>";
	}
	/**
	 * 根据按钮两位数字ID转化为json串中使用的按钮ID，即："01"="btn_01"
	 */
	function getButtonId(id){
		return /^btn_/g.test(id)?id:"btn_"+id;
	}
	/**
	 * 根据菜单四位数字ID转化为json串中使用的菜单ID，即："0001"="menu_0001"
        */
	function getMenuId(id){
		return /^menu_/g.test(id)?id:"menu_"+id;
	}
	/**
	 * 向传入的url中添加参数
	 */
	function addParamOnUrl(url, param){
		if(typeof param === "string"){
			return url+(url.indexOf('?')<0?'?':'&')+param;
		}
		return url;
	}
	/**
	 * 根据传入的obj对象获取选中的行，支持treegrid和datagrid
	 */
	function getSelectRows(obj){
		if(obj.data("treegrid"))
			return obj.treegrid('getSelections');
		else if(obj.data("datagrid"))
			return obj.datagrid('getSelections');
		else return [];
	}
	/**
	 * 根据传入的obj对象获取参数，支持treegrid和datagrid
	 */
	function getOptions(obj){
		if(obj.data("treegrid"))
			return obj.treegrid('options');
		else if(obj.data("datagrid"))
			return obj.datagrid('options');
		else return {};
	}
	/**
	 * 初始化按钮Document
	 */
	function initDom(btn){
		if(!this.buttons[btn.id]){
			btn.el = jQuery(createBtnHtml(btn)).linkbutton({}).appendTo(btn.target);
			return this.buttons[btn.id] = btn;
		}
	}
	/**
	 * 初始化按钮事件
	 */
	function initEvent(btn){
		/**
		 * 如果按钮手动配置了click事件，则绑定手动配置的事件，
		 */
		if(btn.onClick){
			btn.el.click(function(btn){
				return function(e){
					/**
					 * 调用手动配置的click事件，传入event和btn对象
					 */
					btn.onClick.call(this, e, btn);
				};
			}(btn));
			return;
		}
		if(!this.opt.win && !btn.useTab || !this.opt.datagrid) return;
		if(!btn.el) return;
		if(app.adapters[btn.id])btn.el.click(app.adapters[btn.id].call(this, btn));
		else btn.el.click(commonEvent.call(this, btn));
	}
	/**
	 * 通用事件
	 */
	function commonEvent(btn){
		var opt = this.opt;
		return function(e){
			var rows = getSelectRows(opt.datagrid);
			var url = btn.url;
			var title = "";
			if(btn.mustSelect&&btn.onlyOne){
				if(rows.length!=1){
					top.jQuery.messager.alert('错误','对不起！请选择一条记录！','error');
					return;
				}else{
					url = addParamOnUrl(url, "id="+rows[0][btn.idField]);
					title = rows[0][btn.nameField];
					if(btn.filterHandle && btn.filterHandle.call(this, rows[0]) == false)
						return;
				}
			}else if(!btn.mustSelect&&btn.onlyOne){
				if(rows.length>1){
					top.jQuery.messager.alert('错误','对不起！请选择一条记录或不选择！','error');
					return;
				}else if(rows.length==1)
					url = addParamOnUrl(url, "pid="+rows[0][btn.idField]+"&pname="+encodeURIComponent(rows[0][btn.nameField]));
				else url = addParamOnUrl(url, "pid=&pname="+encodeURIComponent("根节点"));
			}
			if(opt.win && !btn.useTab){
				opt.win.show({title:btn.name+(title!=""?"—"+title:""),iconCls:btn.iconCls,href:url}, opt.datagrid);
				opt.win.reSize(btn.winWidth,btn.winHeight);
			}else if(top.Tab){
				top.Tab.addTab({title:btn.name+(title!=""?"—"+title:""), url:url, icon:btn.iconCls},function(){
					opt.datagrid.datagrid('clearSelections').datagrid('reload');
				});
			}
		};
	}
	/**
	 * 查询事件
	 */
	function searchEvent(btn){
		var opt = this.opt;
		return function(e){
			$(this).linkbutton('disable');
			var temp = getOptions(opt.datagrid);
            if(temp&&temp.url)temp.url = temp.url;
            alert(temp.url);
			opt.datagrid.datagrid('clearSelections').datagrid('load', $('.search_input_panel').getParamJson());
		};
	}
	/**
	 * 删除事件
	 */
	function deleteEvent(btn){
		var opt = this.opt;
		return function(e){
			var rows = getSelectRows(opt.datagrid);
			var url = btn.url;
			var ids = "", names = "";
			if(btn.mustSelect&&!btn.onlyOne){
				if(0==rows.length){
					top.jQuery.messager.alert('错误','对不起！请至少选择一条记录！','error');
					return;
				}else{
					for(var i=0,len=rows.length;i<len;i++){
						ids += "," + rows[i][btn.idField];
						if(rows[i][btn.nameField])
							names += ",【" + escapeHtmlTag(rows[i][btn.nameField])+"】";
					}
					ids = ids.replace(/(^[,]*)|([,]*$)/,'');
					names = names.replace(/(^[,]*)|([,]*$)/,'');
				}
			}else{
				if(1!=rows.length){
					top.jQuery.messager.alert('错误','对不起！请选择一条记录！','error');
					return;
				}else ids=rows[0][btn.idField];
			}
			jQuery.messager.confirm('提示','确定删除'+(names.length>0?names+",":"")+"共<b>"+rows.length+'</b>条记录吗？',function(b){
				if(!b) return;
				jQuery.ajax({
					async: false,
					type: "GET",
					dataType: "json",
					url: addParamOnUrl(url, "id="+ids),
					success: function(data){
						if(data&&!data.b)jQuery.messager.alert('删除失败',data.msg+'<br/><br/>请刷新页面重试！','error');
						opt.datagrid.datagrid('reload');
						opt.datagrid.datagrid('clearSelections');
					},
					error:function(){
						jQuery.messager.alert('错误','删除发生错误！','error')
					}
				});
			});
		};
	}
	/**
	 * 重置密码事件
	 */
	function resetPassEvent(btn){
		var opt = this.opt;
		return function(e){
			var rows = getSelectRows(opt.datagrid);
			var url = btn.url;
			var ids = "";
			if(btn.mustSelect&&!btn.onlyOne){
				if(0==rows.length){
					top.jQuery.messager.alert('错误','对不起！请至少选择一条记录！','error');
					return;
				}else{
					for(var i=0,len=rows.length;i<len;i++){
						ids += "," + rows[i][btn.idField];
					}
					ids = ids.replace(/(^[,]*)|([,]*$)/,'');
				}
			}else{
				if(1!=rows.length){
					top.jQuery.messager.alert('错误','对不起！请选择一条记录！','error');
					return;
				}else ids=rows[i][btn.idField];
			}
			jQuery.messager.confirm('提示','确定重置'+rows.length+'位用户的密码吗？',function(b){
				if(!b) return;
				jQuery.ajax({
					async: false,
					type: "GET",
					dataType: "json",
					url: addParamOnUrl(url, "id="+encodeURIComponent(ids)),
					success: function(data){
						if(data&&!data.b)jQuery.messager.alert('重置失败',data.msg+'<br/><br/>请刷新页面重试！','error');
						opt.datagrid.datagrid('reload');
						opt.datagrid.datagrid('clearSelections');
					},
					error:function(){
						jQuery.messager.alert('错误','重置发生错误！','error')
					}
				});
			});
		};
	}
	
	var app = function(opt){
		return new app.fn.init(opt);
	};
	app.fn = app.prototype = {
		init : function(opt){
			opt = opt || {};
			this.opt = opt = jQuery.extend({}, app.defaults, opt);
            if(opt.buttons && 0 < opt.buttons.length){
                for(var i=0,len=opt.buttons.length;i<len;i++){
                    var btn = opt.buttons[i];
                    btn.id = getButtonId(btn.id);//btn_01
                    btn = jQuery.extend({}, app.btnDefaults,btn);
                    initDom.call(this, btn);
                    initEvent.call(this, btn);
//                    btn.id = getButtonId(btn.id);
//                    btn = jQuery.extend({}, app.btnDefaults, btn);
//
//                    initEvent.call(this, initDom.call(this, btn));

                }
            }
/*
			if(!App.resource) return this;

			opt.menu = App.resource[getMenuId(opt.moduleId)];

			if(!opt.menu) return this;
			if(opt.buttons && 0 < opt.buttons.length){
				for(var i=0,len=opt.buttons.length;i<len;i++){
					var btn = opt.buttons[i];
					btn.id = getButtonId(btn.id);//btn_01
					if(!opt.menu.buttons[btn.id]) continue;
					btn = jQuery.extend({}, app.btnDefaults, opt.menu.buttons[btn.id], btn);
					initDom.call(this, btn);
					initEvent.call(this, btn);
				}
			}
			if(opt.defaultSite){
				for(var btnId in opt.menu.buttons){
					var btn = opt.menu.buttons[btnId];
					if(!btn) continue;
					btn.id = getButtonId(btn.id);
					btn = jQuery.extend({}, app.btnDefaults, btn);
					if(!this.buttons[btn.id]){
						initEvent.call(this, initDom.call(this, btn));
					}
				}
			}*/
			return this;
		},
		getButton : function(id){
			var id = getButtonId(id);
			if(this.buttons[id]) return this.buttons[id];
			if(this.opt.menu&&this.opt.menu.buttons&&this.opt.menu.buttons[id])
				return this.opt.menu.buttons[id];
			return null;
		},
		getButtonString : function(id, callback){
			var id = getButtonId(id);
			if(this.opt.menu&&this.opt.menu.buttons&&this.opt.menu.buttons[id])
				return callback(this.opt.menu.buttons[id]);
			else return "";
		},
		opt : null,		//配置
		buttons : {}	//已初始化的按钮
	};
	/**
	 * 默认配置
	 */
	app.defaults = {
		moduleId : request("mid"),		//模块编号，默认取url上的mid值
		defaultSite : ".button_panel",	//动作按钮的默认位置
		win : null,					//窗口对象
		datagrid : null,			//数据对象
		menu : {},
		buttons : []
	};
	/**
	 * 按钮默认配置
	 */
	app.btnDefaults = {
		id:"",				//动作id
		name:"",			//动作名称
		iconCls:"",			//动作样式
		desc:"",			//动作描述
		target:app.defaults.defaultSite,	//位置
		onClick:null,		//单击事件
		filterHandle:null,	//当使用commonEvent方法作为单击事件时，如果是必须只能选择一项则掉用该方法，返回false不触发单击事件
		el:null,			//jQuery DOM object
		
		idField:"id",		//用于获取datagrid中选中行的唯一标识
		nameField:"name",	//用于获取datagrid中选中行的名称
		url:"",				//动作连接地址
		mustSelect:false,	//必须有选择
		onlyOne: true,		//只能选择一个
		useTab: false,		//是否使用top层的tab控件创建窗口，如果是请注意打开后的页面确定和取消事件
		winWidth:200,		//打开窗口的宽度
		winHeight:200		//打开窗口的高度
	}
	/**
	 * 按钮配置信息，主要由过滤器依据权限动态创建
	 */
	app.resource = {};
	/**
	 * 适配器，用于注册按钮处理事件
	 * 适配器必须返回一个function作为click事件处理方法
	 */
	app.adapters = {
		btn_01:commonEvent,		//新增
		btn_02:commonEvent,		//编辑
		btn_03:commonEvent,		//浏览
		btn_04:deleteEvent,		//删除
		btn_05:searchEvent,		//查询
		btn_09:commonEvent,		//授权
        btn_13:resetPassEvent	//重置密码
	};
	app.fn.init.prototype = app.fn;
	window.App = app;
})(jQuery);