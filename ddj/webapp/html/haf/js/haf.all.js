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

})(window, undefined, jQuery);/**
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
/**
 * use bootstrap js
 */
(function(window, undefined, $){

    if(!window.HAF) return;
    if(!window.HAF.Dialog) window.HAF.Dialog = {};

    var _url_psn_dept = "/oa/service/comorg/getOrgChildren.do";
    var _url_psn_by_dept = '/oa/service/comorg/getPsnByDept.do';
    var _HTMLTemplate;

    var _z_index = 1000;

    var _DialogCache = {};

    function _modal(options){
        var $dialog;
        var options = $.extend({
            backdrop: "static", title: "&nbsp;", content: "&nbsp;", width: "650px", forceInit: true
        }, options);
        var key = "_haf_dialog_";

        var _init = function(){
            if(options.title) $dialog.find(".modal-title").empty().append(options.title);
            if(options.content) $dialog.find(".modal-body").empty().append(options.content);
            if(typeof options.ready == "function"){
                options.ready($dialog);
            }
            if(typeof options.success == "function"){
                $dialog.find(".btn-primary").unbind("click").click(function(){
                    var s = options.success($dialog);
                    if(!(typeof s=="boolean" && s==false)){
                        $dialog.modal("toggle");
                    }
                });
            }
        }

        if(options.key){
            key += options.key;
            $dialog = $("#"+key);
            if($dialog.size()>0){
                $dialog.modal("toggle");
                if(options.backdrop) $dialog.data('bs.modal').$backdrop.css("z-index", _z_index++);
                $dialog.data('bs.modal').$element.css("z-index", _z_index++);
                if(options.forceInit){ _init(); }
                return;
            }
        }else{
            key += (new Date()).getTime();
        }

        var html = '<div id="' + key + '" class="modal fade" data-backdrop="' + options.backdrop + '">' +
            '<div class="modal-dialog haf-dialog" style="width:' + options.width + '">' +
            '<div class="modal-content">' +
            '<div class="modal-header">' +
            '<button type="button" class="close" data-dismiss="modal">&times;</button>' +
            '<h4 class="modal-title">' +
            //options.title +
            '</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            //options.content +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-default" data-dismiss="modal">关&nbsp;闭</button>' +
            '' + (typeof options.success == "function" ? '<button type="button" class="btn btn-primary">确&nbsp;定</button>' : '') +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>';

        $dialog = $(html);
        $dialog.appendTo("body");

        _init();

        $dialog.modal("toggle");
        if(options.backdrop) $dialog.data('bs.modal').$backdrop.css("z-index", _z_index++);
        $dialog.data('bs.modal').$element.css("z-index", _z_index++);
    }

    //人员选择框
    function _selectPsn(_options){

        var options = $.extend({
            backdrop: "static", //模态窗口背景遮罩，false不显示，true点击隐藏，static静态
            rootId: "COM_ROOT", //树默认根节点组织
            rootSelect: true,   //是否允许选择根组织【根，向上一级，到我的部门】
            deptLoad: "",       //默认加载（人员）的组织
            defaultMyDept: true,       //当!deptLoad是默认用户所在部门
            singleSelect: false //是否单选
        }, _options);

        var selectedData = {};
        var treeUrl = HAF.basePath() + _url_psn_dept;
        var gridUrl = HAF.basePath() + _url_psn_by_dept;
        if(!options.deptLoad) options.deptLoad = options.rootId;

        var $tree ,$treeBtns ,$grid ,$selected ,$selectedBtns;
        var treeBtnIndex = -1;

        _modal({
            key: "psn_sel",
            title: "人员选择",
            content: _HTMLTemplate.selectPsn,
            backdrop: options.backdrop,
            width: "700px",
            ready: function($dialog){

                $tree = $dialog.find("div[haf-comp-id='deptTree'] ul");
                $treeBtns = $dialog.find("div[haf-comp-id='deptTreeBtns'] .dropdown-menu li");
                $grid = $dialog.find("div[haf-comp-id='personList'] table");
                $selected = $dialog.find("div[haf-comp-id='personSelected']");
                $selectedBtns = $dialog.find("div[haf-comp-id='personSelectedBtns']");

                //dept tree
                $tree.tree({
                    lines: true,
                    url: treeUrl + "?orgId=" + options.rootId + "&getSelf=true",
                    onBeforeExpand: function(node){
                        $tree.tree('options').url = treeUrl + "?getSelf=false&orgId=" + node.id;
                    },
                    onSelect: function(node){
                        $grid.datagrid('load',{deptId: node.id, myDept: options.defaultMyDept});
                    }
                });

                //tree btn
                if(options.rootSelect){
                    $treeBtns.parent().prev().css("visibility","visible");
                }else{
                    $treeBtns.parent().prev().css("visibility","hidden");
                }
                $treeBtns.click(function(){
                    var root = $tree.tree("getRoot");
                    var btnIndex = $(this).index();
                    var treeUrl1 = "";
                    if(btnIndex==0 && root.id!="COM_ROOT"){
                        treeUrl1 = treeUrl + "?orgId=COM_ROOT&getSelf=true";
                    }else if(btnIndex==1 && root.id!="COM_ROOT"){
                        treeUrl1 = treeUrl + "?orgId=" + root.id + "&getSelf=true&getParent=true";
                    }else if(btnIndex==2 && btnIndex!=treeBtnIndex){
                        treeUrl1 = treeUrl + "?orgId=" + options.deptLoad + "&getSelf=true&myDept=true";
                    }
                    if(treeUrl1){
                        $.get(treeUrl1 + "&t" + (new Date()).getTime(), "", function(data){
                            $tree.tree({url: null, data: data});
                            root = $tree.tree("getRoot");
                            if(root!=null) $tree.tree("select", root.target)
                        });
                    }
                });

                //person list
                $grid.datagrid({
                    fit: true,
                    striped: true,
                    method: "POST",
                    rownumbers: true,
                    border: false,
                    checkbox: true,
                    singleSelect: options.singleSelect,
                    url: gridUrl + "?t" + (new Date()).getTime(),
                    queryParams: {deptId: options.deptLoad, myDept: options.defaultMyDept},
                    idField: 'id',
                    frozenColumns: [[
                        {field:'id',checkbox:true},
                        {field:'name',title:'姓名',width:70,align:'center'},
                        {field:'empNumber',title:'工号',width:70,align:'center'}
                    ]],
                    columns: [[
                        /*{field:'positionId',title:'职位',width:120,align:'center'},*/
                        {field:'positionName',title:'职位',width:120,align:'center'},
                        /*{field:'departmentId',title:'部门',width:120,align:'center'},*/
                        {field:'departmentName',title:'部门',width:120,align:'center'},
                        /*{field:'postLevelId',title:'职级',width:100,align:'center'},*/
                        /*{field:'postLevelName',title:'职级',width:100,align:'center'},*/
                        {field:'officePhone',title:'办公电话',width:100,align:'center'},
                        {field:'mobile',title:'移动长号',width:100,align:'center'}
                    ]],
                    onSelect: function(rowIndex, rowData){
                        var span = $selected.find("span[dataId='"+rowData.id+"']");
                        if(span.size()==0){
                            if(options.singleSelect){
                                $selected.children().remove();
                                selectedData = {};
                            }
                            $selected.append("<span dataId='"+rowData.id+"'>"+rowData.name+"["+rowData.empNumber+"]</span>");
                            selectedData[rowData.id] = rowData;
                        }
                    },
                    onUnselect: function(rowIndex, rowData){
                        var span = $selected.find("span[dataId='"+rowData.id+"']");
                        if(span.size()>0) span.remove();
                    },
                    onSelectAll: function(rows){
                        $.each(rows,function(rowIndex, rowData){
                            var span = $selected.find("span[dataId='"+rowData.id+"']");
                            if(span.size()==0){
                                $selected.append("<span dataId='"+rowData.id+"'>"+rowData.name+"["+rowData.empNumber+"]</span>");
                                selectedData[rowData.id] = rowData;
                            }
                        });
                    },
                    onUnselectAll: function(rows){
                        $.each(rows,function(rowIndex, rowData){
                            var span = $selected.find("span[dataId='"+rowData.id+"']");
                            if(span.size()>0) span.remove();
                        });
                    }
                });

                //person selected
                $selected.click(function(e){
                    var _t = $(e.target);
                    if(_t.is("span")){
                        if(_t.is(".selected")){
                            _t.removeClass("selected");
                        }else{
                            _t.addClass("selected");
                        }
                    }
                });

                //person selected btns
                //var btns = $selectedBtns.find("a");
                $selectedBtns.find("a").click(function(){
                    var selecteds = $selected.find(".selected");
                    var sSize = selecteds.size();
                    var btnIndex = $(this).index();
                    if(btnIndex==5){
                        $selected.find("span:not(.selected)").addClass("selected");
                    }else if(sSize==0){
                        return;
                    }
                    var selectAll = sSize==$selected.find("span").size();
                    if(btnIndex==0 && !selectAll){
                        var _c = selecteds.clone();
                        selecteds.remove();
                        _c.insertBefore($selected.find("span:first"));
                    }else if(btnIndex==3 && !selectAll){
                        var _c = selecteds.clone();
                        selecteds.remove();
                        _c.insertAfter($selected.find("span:last"));
                    }else if(btnIndex==1 && !selectAll){
                        selecteds.not(":first-child").each(function(){
                            var _c = $(this).clone();
                            var _p = $(this).prev();
                            if(!_p.is(".selected")){
                                _c.insertBefore(_p);
                                $(this).remove();
                            }
                        });
                    }else if(btnIndex==2 && !selectAll){
                        var reverse = []; //倒序
                        selecteds = selecteds.not(":last-child");
                        for(var i=selecteds.size()-1;i>=0;i--){
                            reverse.push(selecteds[i]);
                        }
                        for(var i=0;i<reverse.length;i++){
                            var _c = $(reverse[i]).clone();
                            var _n = $(reverse[i]).next();
                            if(!_n.is(".selected")){
                                _c.insertAfter(_n);
                                $(reverse[i]).remove();
                            }
                        };
                    }else if(btnIndex==4){
                        selecteds.remove();
                        $grid.datagrid("clearSelections");
                    }else if(btnIndex==6){
                        selecteds.removeClass("selected");
                    }
                });
                //open dialog
                $dialog.find("a[href='#']").attr("href","javascript:void(0)");
            },
            success: function($dialog){
                var _data = [];
                $selected.find("span").each(function(){
                    _data.push(selectedData[$(this).attr("dataId")]);
                });
                if(_data.length==0){
                    alert("尚未选择任何人员");
                    return false;
                }
                if(typeof options.success == "function"){
                    $dialog.modal('toggle');
                    options.success(_data);
                }
                //禁止调用_modal本身的‘toggle’
                return false;
            }
        });
    }

    //组织结构选择框
    function _selectOrg(options){

    }

    //
    _HTMLTemplate = {
        selectPsn:
            '<table class="person-select">' +
            '<tr>' +
            '<td>' +
            '<div haf-comp-id="deptTreeBtns">' +
            '<div class="dropdown">' +
            '<a href="#" data-toggle="dropdown">' +
            '<span class="glyphicon glyphicon-list"></span>&nbsp;' +
            '<span haf-comp-id="deptLabel"></span>&nbsp;' +
            '<span class="caret"></span>' +
            '</a>' +
            '<ul class="dropdown-menu">' +
            '<li><a href="#">股份公司</a></li>' +
            '<li><a href="#">上级部门</a></li>' +
            '<li><a href="#">我的部门</a></li>' +
            '</ul>' +
            '</div>' +
            '</div>' +
            '<div haf-comp-id="deptTree"><div><ul></ul></div></div>' +
            '</td>' +
            '<td><div haf-comp-id="personList"><table></table></div></td>' +
            '<td>' +
            '<div haf-comp-id="personSelected"></div>' +
            '<div haf-comp-id="personSelectedBtns">' +
            '<a href="#"><span class="glyphicon glyphicon-open"></span></a>' +
            '<a href="#"><span class="glyphicon glyphicon-arrow-up"></span></a>' +
            '<a href="#"><span class="glyphicon glyphicon-arrow-down"></span></a>' +
            '<a href="#"><span class="glyphicon glyphicon-download-alt"></span></a>' +
            '<a href="#"><span class="glyphicon glyphicon-remove"></span></a>' +
            '<a href="#"><span class="glyphicon glyphicon-saved"></span></a>' +
            '<a href="#"><span class="glyphicon glyphicon-share-alt"></span></a>' +
            '</div>' +
            '</td>' +
            '</tr>' +
            '</table>'
        ,
        flowConfirm:
            '<div class="form-horizontal flow-confirm">' +
            '<div class="form-group">' +
            '<label class="col-xs-3 control-label">操作类型</label>' +
            '<div class="col-xs-8">' +
            '<input class="form-control" haf-comp-id="operateName" readonly>' +
            '</div>' +
            '</div>' +
            '<div class="form-group" haf-comp-id="nextNodeRow">' +
            '<label class="col-xs-3 control-label">下一处理环节<span></span></label>' +
            '<div class="col-xs-8">' +
            '<input class="form-control"  haf-comp-id="nextNodeName" readonly>' +
            '</div>' +
            '</div>' +
            '<div class="form-group" haf-comp-id="nextPersonRow">' +
            '<label class="col-xs-3 control-label">下一处理人<span></span></label>' +
            '<div class="col-xs-7">' +
            '<input class="form-control"  haf-comp-id="nextPersonName" readonly>' +
            '<input class="form-control" type="hidden" haf-comp-id="nextPersonUsername" readonly>' +
            '</div>' +
            '<div class="col-xs-2">' +
            '<button class="btn btn-info" haf-comp-id="authorSelector">选择…</button>' +
            '</div>' +
            '</div>' +
            '<div haf-comp-id="commentsAddRow" class="form-group">' +
            '<label class="col-xs-3 control-label">批示意见</label>' +
            '<div haf-comp-id="commentsAdd" class="col-xs-8">' +
            '<span class="btn btn-link">&nbsp;添加&gt;&gt;</span>' +
            '</div>' +
            '</div>' +
            '<div class="form-group" haf-comp-id="flowCommentRow">' +
            '<label class="col-xs-3 control-label">批示意见</label>' +
            '<div class="col-xs-8">' +
            '<textarea class="form-control"  haf-comp-id="flowComment" rows="3"></textarea>' +
            '</div>' +
            '<div class="col-xs-8 col-xs-offset-3" haf-comp-id="tempComments">' +
            '<span>同意</span><span>批准</span><span>不同意</span><span>返回再议</span><span>请处理</span><span>请尽快执行</span>' +
            '</div>' +
            '</div>' +
            '<div class="form-group" haf-comp-id="flowMessageRow">' +
            '<label class="col-xs-3 control-label">消息提醒</label>' +
            '<div class="col-xs-8">' +
            '<input class="form-control"  haf-comp-id="flowMessage">' +
            '</div>' +
            '</div>' +
            '</div>'
        ,
        createComm:
            '<div class="form-horizontal">' +
            '<input type="hidden" haf-comp-id="refId">' +
            '<input type="hidden" haf-comp-id="refKey">' +
            '<div class="form-group">' +
            '<label class="col-xs-3 control-label">消息主题</label>' +
            '<div class="col-xs-8">' +
            '<input class="form-control" haf-comp-id="subject">' +
            '</div>' +
            '</div>' +
            '<div class="form-group">' +
            '<label class="col-xs-3 control-label">回复人<span></span></label>' +
            '<div class="col-xs-6" >' +
            '<input class="form-control"  haf-comp-id="replierName" readonly>' +
            '<span style="display:none"><input haf-comp-id="replierId"></span>' +
            '</div>' +
            '<div class="col-xs-2">' +
            '<button class="btn btn-info" haf-comp-id="replierSelector">选择…</button>' +
            '</div>' +
            '</div>' +
            '<div class="form-group" haf-comp-id="contentRow">' +
            '<label class="col-xs-3 control-label">沟通内容</label>' +
            '<div class="col-xs-8">' +
            '<textarea class="form-control"  haf-comp-id="content" rows="3"></textarea>' +
            '</div>' +
            '</div>' +
            '</div>'
        ,
        replyComm:
            '<div class="form-horizontal comm-reply">' +
            '<input type="hidden" haf-comp-id="id">' +
            '<div class="form-group">' +
            '<label class="col-xs-3 control-label">发起人</label>' +
            '<div class="col-xs-6">' +
            '<input class="form-control" haf-comp-id="creatorName" readonly>' +
            '</div>' +
            '<div class="col-xs-2">' +
            '<span haf-comp-id="ref-form" class="btn btn-link">关联业务表单</span>' +
            '</div>' +
            '</div>' +
            '<div class="form-group">' +
            '<label class="col-xs-3 control-label">主题</label>' +
            '<div class="col-xs-8">' +
            '<input class="form-control" haf-comp-id="subject" readonly>' +
            '</div>' +
            '</div>' +
            '<div class="form-group">' +
            '<label class="col-xs-3 control-label">沟通内容</label>' +
            '<div class="col-xs-8">' +
            '<textarea class="form-control"  haf-comp-id="content" rows="3" readonly></textarea>' +
            '</div>' +
            '</div>' +
            '<div class="form-group">' +
            '<label class="col-xs-3 control-label">回复内容</label>' +
            '<div class="col-xs-8">' +
            '<textarea class="form-control"  haf-comp-id="replyContent" rows="3"></textarea>' +
            '</div>' +
            '</div>' +
            '</div>'
    };

    //
    var _dialogs = {
        modal: function(options){_modal(options)},
        selectPsn: function(options){_selectPsn(options)},
        selectOrg: function(options){_selectOrg(options)},
        HTMLTemplate: _HTMLTemplate
    };
    var _dialog = function(cmd,opt){
        if(typeof cmd=="string"){
            _dialogs[cmd](opt);
        }else{
            var opt = cmd || {};
            _modal(opt);
        }
    }

    /* HAF dialog extend */
    $.extend(window.HAF.Dialog, _dialogs);
    $.extend(window.HAF, {
        dialog: function(cmd,opt){_dialog(cmd,opt);}
    });

})(window, undefined, jQuery);
/**
 * Created by zhangjunhui on 13-12-18.
 * version: 1.0
 */
(function(window, undefined, $){

    if(!window.HAF) return;
    if(!window.HAF.Flow) window.HAF.Flow = {};

    //flow ids
    var _ids = {
        flowDefId: "", flowKeyId: "", instId: "", taskUserId: "", dataId: "", nodeId: "", formId: "", executionId: ""
    };

    var _currentUsername = "", _currentDate = "", _currentPerson = {name:"",departmentId:"",sysUsername:""};

    //基础信息、环节配置，流程实例，环节按钮，当前环节，当前任务，……
    var _flowBase = null, _flowInst = null, _currentNode = null, _currentTask = null;
    var _flowNodes = null, _nodeButtons = null, _currentNodes = null, _currentTasks = null, _flowInsts = null, _authorizedTasks = null;
    var _isTaskPool = false, _isClaimedTask = false;

    //_flowNodes，_nodeButtons 转成 id为key的JSON对象
    var _oFlowNodes = null, _oNodeButtons = null;

    //历史流程环节，流程批示意见, 环节日志， 操作日志，任务日志
    var _flowHiTasks = null, _flowHiActions = null, _flowHiNodes = null, _flowHiComments;

    //流程运行中信息
    var _flowContext = {
        assigners:{}, processVar:{}, subProcessVar:{}
    };
    //业务数据收集
    var _bizData = {};
    //缓存提交时获取的下一环节，用于beforeCommit时使用
    var _nextNodes = null;
    //是否有授权
    var _isMyAuthorized = false;
    //是否新流程
    var _isNewFlow = true;

    var _reject_menu_init = false;  //驳回菜单是否初始化：避免重复初始化
    var _append_menu_init = false;  //加签菜单是否初始化：避免重复初始化
    var _jump_menu_init = false;    //跳转菜单是否初始化：避免重复初始化
    var _hangup_menu_init = false;  //挂起菜单是否初始化：避免重复初始化
    var _classify_menu_init = false;  //归类菜单是否初始化：避免重复初始化
    var _reauthorize_menu_init = false;  //收回菜单是否初始化：避免重复初始化

    /** urls
     * 相关url
     */
    var _urls = {
        "init": "/oa/service/flow/init.do",
        "submit": "/oa/service/flow/submit.do",
        "view": "/oa/service/flow/view/img.do",
        "get_node": "/oa/service/flow/getFlowNodes.do",
        "get_next_node": "/oa/service/flow/getNextNode.do",
        "get_node_user": "/oa/service/flow/getNodeUser.do",
        "get_history": "/oa/service/flow/getHistory.do",
        "single_claim": "/oa/service/flow/singleClaim.do",
        "classify": "/oa/service/flow/classify.do",
        "task_redirect": "/oa/service/flow/userTask/redirect.do",
        "create_comm": "/oa/service/comm/create.do",
        "get_comm": "/oa/service/comm/my/get.do"
    };

    /** _options
     * 流程初始配置信息
     * startNode: StartEvent, 流程图中的开始圆圈
     * firstNode: FirstNode， 流程中第一个操作环节（申请人环节）
     * endNode: EndEvent, 流程图中的最后一个结束圆圈
     * lastNode：LastNode，流程最后一个审批环节（多条分支指向结束环节时，无效）
     */
    var _options = {
        ids: {},                       //流程相关关键ID
        submitUrl: "",
        startNode: "StartEvent",   //流程开始：原点（圈圈）
        endNode: "EndEvent",       //流程结束：结点（圈圈）
        firstNode: "FirstNode",     //流程开始环节：第一个任务节点
        lastNode: "LastNode",       //流程结束环节：最后一个任务节点
        //onXxxx: null,                 //操作执行时回调方法：return true,false
        beforeCommit: null,         //提交后台前执行方法：return true,false
        afterCommit: null,          //成功执行后方法：return true,false
        parseProcessVar: null,      //流程变量处理方法：return json
        parseSubProcessVar: null,   //子流程变量处理方法：return json
        parseBizData: null          //业务数据处理方法：return json
    };

    //流程执行相关确认信息
    /*var _confirms = {
        nextNodes: []
    };
    function _resetConfirms(){
        _confirms = $.extend({},{
           nextNodes: [], operaterType: _getOperateType(), operateName: _getButtonText()
        });
    }*/

    /** _buttons
     * 流程操作按钮默认信息
     */
    var _buttons = {
        quit: {key: "quit", text: "退出", clsClass: "btn-info"},
        save: {key: "save", text: "保存", clsClass: "btn-primary"},
        submit: {key: "submit", text: "提交", clsClass: "btn-success", confirm: true},
        reject: {key: "reject", text: "驳回", clsClass: "btn-warning", hasMenu: true, confirm: true},
        append: {key: "append", text: "加签", clsClass: "btn-info", hasMenu: true},
        handover: {key: "handover", text: "转办", clsClass: "btn-info"},
        revoke: {key: "revoke", text: "撤签", clsClass: "btn-info"},
        notify: {key: "notify", text: "通知", clsClass: "btn-info"},
        deliver: {key: "deliver", text: "传阅", clsClass: "btn-info"},
        authorize: {key: "authorize", text: "授权", clsClass: "btn-info"},
        reauthorize: {key: "reauthorize", text: "收回授权", clsClass: "btn-info"},
        claim: {key: "claim", text: "认领", clsClass: "btn-info"},
        backpool: {key: "backpool", text: "退回任务池", clsClass: "btn-info"},
        hangup: {key: "hangup", text: "挂起", clsClass: "btn-info"},
        classify: {key: "classify", text: "归类", clsClass: "btn-info", hasMenu: true},
        communicate: {key: "communicate", text: "沟通", clsClass: "btn-info"},
        jump: {key: "jump", text: "跳转", clsClass: "btn-warning", hasMenu: true, confirm: true},
        replace: {key: "replace", text: "替换", clsClass: "btn-warning"},
        stop: {key: "stop", text: "终止", clsClass: "btn-warning"},
        manage: {key: "manage", text: "权限管理", clsClass: "btn-warning"},
        print: {key: "print", text: "打印", clsClass: "btn-info"},
        view: {key: "view", text: "查看流程", clsClass: "btn-info"}
    }
    //默认显示的按钮
    var _buttons_def = ["quit","print","view"];
    //当新建流程时显示的按钮
    var _buttons_new = ["save","submit"];
    //加签类型
    var _appendTypes = {
        "before": "加签到我之前", "current": "加签与我会签", "after": "加签到我之后"
    };
    //默认的任务分类
    var _classifyList = [
        {name:"important",text:"重要任务"},
        {name:"normal",text:"普通任务"},
        {name:"issue",text:"问题任务"}
    ];
    //挂起操作类型值
    var _classify_hangup = "[HANGUP]";
    //message定义
    var _messages = {};
    function _initDefMessages(){
        var personName = _currentPerson.name;
        var flowName = _flowBase.name;
        _messages = {
            "submit": personName + _getButtonText("submit") + "了" + flowName + "，请处理",
            "reject": personName + _getButtonText("reject") + "了您的" + flowName + "，请查阅",
            "append": personName + _getButtonText("append") + "给您了" + flowName + "，请处理",
            "handover": personName + "将" + flowName + "转给您处理" + "，请查阅",
            "notify": personName + _getButtonText("notify") + "您及时处理" + flowName + "，请查阅",
            "deliver": personName + "传给您的" + flowName + "，请查阅",
            "authorize": personName + "授权您处理" + flowName + "，请查阅",
            "jump": personName + "将" + flowName + "跳转给您，请处理",
            "replace": personName + "将" + flowName + "处理人替换为您，请处理"
        };
    }

    //-- private (common) functions --------------------------
    //flow ids
    function _setIds(k, v){
        HAF.setKeyValue(_ids, k, v);
    }
    function _getIds(k){
        return HAF.getKeyValue(_ids, k);
    }
    /**
     * 判断开始、结束环节
     */
    function _isStartNode(nodeId){
        var nodeId = nodeId || _getIds("nodeId");
        return nodeId && nodeId==_options.startNode;
    }
    function _getStartNode(){
        return _options.startNode || "";
    }
    function _setStartNode(nodeId){
        _options.startNode = nodeId;
    }
    //endNode
    function _isEndNode(nodeId){
        var nodeId = nodeId || _getIds("nodeId");
        if(nodeId && _options.endNode){
            var a = _options.endNode.split(",");
            for(var i=0;i< a.length;i++){
                if(nodeId==a[i]) return true;
            }
        }
        return false;
    }
    function _getEndNode(){
        return _options.endNode || "";
    }
    function _setEndNode(nodeId){
        _options.endNode = nodeId;
    }
    //firstNode
    function _isFirstNode(nodeId){
        var nodeId = nodeId || _getIds("nodeId");
        return nodeId && nodeId==_options.firstNode;
    }
    function _getFirstNode(){
        return _options.firstNode || "";
    }
    function _setFirstNode(nodeId){
        _options.firstNode = nodeId;
    }
    //lastNode
    function _isLastNode(nodeId){
        var nodeId = nodeId || _getIds("nodeId");
        if(nodeId && _options.lastNode){
            var a = _options.lastNode.split(",");
            for(var i=0;i< a.length;i++){
                if(nodeId==a[i]) return true;
            }
        }
        return false;
    }
    function _getLastNode(){
        return _options.lastNode || "";
    }
    function _setLastNode(nodeId){
        _options.lastNode = nodeId;
    }
    //操作类型
    function _setOperateType(operateType){
        _flowContext.operateType = operateType;
    }
    function _getOperateType(){
        return _flowContext.operateType;
    }
    //操作按钮
    function _getButton(operateType){
        var operateType = operateType || _getOperateType();
        return _buttons[operateType];
    }
    function _getButtonText(operateType){
        var operateType = operateType || _getOperateType();
        if(_buttons[operateType]){
            return _buttons[operateType].text;
        }else{
            return operateType || "";
        }
    }
    function _getButtonValue(operateType){
        var operateType = operateType || _getOperateType();
        return _buttons[operateType].value;
    }
    //
    function _getNodeValue(id, k){
        if(!id) return "";
        var node = _oFlowNodes[id];
        if(node){
            if(k) return node[k]||"";
            else return node;
        }else{
            return "";
        }
    }
    //FlowContext sets
    function _initContext(){
        _flowContext = $.extend({assigners:{}, processVar:{}, subProcessVar:{}}, _ids);
    }
    function _resetContext(){
        _initContext();
        for(var i=0;i<arguments.length;i++){
            if(typeof arguments[i] == "string"){
                _flowContext.operateType = arguments[i];
            }else{
                $.extend(_flowContext, arguments[i]);
            }
        }
        if(_currentTask){
            _flowContext.operater = _currentTask.assignerId;    //默认以任务处理人身份操作流程（一般只有本人能打开自己的任务）
        }
    }
    function _setContext(k, v){
        HAF.setKeyValue(_flowContext, k, v);
    }
    function _setAssigner(node, user){
        HAF.setKeyValue(_flowContext.assigners, node, user);
    }
    function _setProcessVar(k, v){
        HAF.setKeyValue(_flowContext.processVar, k, v);
    }
    function _setSubProcessVar(k, v){
        HAF.setKeyValue(_flowContext.subProcessVar, k, v);
    }
    function _setBizData(k, v){
        HAF.setKeyValue(_bizData, k, v);
    }
    //flow datas
    function _getFlowBase(){
        return _flowBase;
    }
    function _getFlowNodes(){
        return _flowNodes;
    }
    function _getCurrentNode(){
        var nodeId = _getIds("nodeId");
        if(nodeId) return _oFlowNodes[nodeId];
        else return {};
    }
    function _getCurrentUsername(){
        return _currentUsername;
    }
    function _getCurrentDate(){
        return _currentDate;
    }
    function _getCurrentPerson(){
        return _currentPerson;
    }
    function _getCurrentDept(){
        return _currentPerson.departmentId;
    }
    function _getFlowMessage(){
        var operateType = arguments[0] || _getOperateType();
        return _messages[operateType];
    }
    function _setNextNodes(nodes){
        _nextNodes = nodes;
    }
    function _getNextNodes(){
        return _nextNodes;
    }
    //
    function _getValue(obj,key){
        return obj[key] || "";
    }

    //disabled button seconds
    function _disableButtons(){
        $(".flow-form-header .flow-buttons .btn").each(function(){
            this.disabled = true;
        });
        window.setTimeout(function(){
            $(".flow-form-header .flow-buttons .btn").each(function(){
                this.disabled = false;
            });
        },3000);
    }

    //-- flow operate functions -----------------------
    /** _init
     * 流程初始化
     * options 配置信息
     */
    function _init(options){
        //options参数初始化
        $.extend(true, _options, options);
        //初始化urls
        $.each(_urls,function(k,v){
            _urls[k] = HAF.basePath() + v;
        });
        //读取options中的_ids
        _setIds(_options.ids);
        //替换指定的submitUrl
        if(_options.submitUrl){
            _urls.submit = HAF.basePath() + _options.submitUrl;
        }
        //获取流程及权限信息
        HAF.ajax({
            url: _urls.init,
            data: _ids,
            success: function(data){
                var initSuccess = _initData(data);
                data = null;
                if(initSuccess){
                    //自定义消息提示
                    if(_options.flowMessage){
                        $.extend(_messages, _options.flowMessage);
                    }
                    //计算操作权限，装载初始信息
                    if(_oNodeButtons){
                        //初始化表单按钮
                        $(".flow-form-header").append('<div class="btn-toolbar flow-buttons"><div class="btn-group"></div></div>');
                        var $bar = $(".flow-form-header .flow-buttons .btn-group");
                        $.each(_buttons,function(k,o){
                            if(_oNodeButtons[k]){
                                var $btn = $('<button class="btn">'+o.text+'</button>');
                                var $grp = $('<div class="btn-group flow-btn-'+k+'"></div>');
                                $grp.append($btn).appendTo($bar);
                                if(o.clsClass) $btn.addClass(o.clsClass);
                                $btn.click(function(){ _command($(this),k); });
                            }
                        });
                    }
                    //流程审批历史
                    if(_flowHiComments && _flowHiComments.length){
                        var $history = $("div.flow-user-comments");
                        if($history.size()>0){
                            $.each(_flowHiComments, function(i,dd){
                                $history.append('<div class="form-group">' +
                                    '<label class="col-xs-2 control-label">' + _getValue(dd,"nodeName") + '</label>' +
                                    '<div class="col-xs-7">' +
                                    '<p class="form-control-static">' + _getValue(dd,"operaterName") + '</p>' +
                                    '</div>' +
                                    '<div class="col-xs-1">' +
                                    '<p class="form-control-static">' + _getButtonText(_getValue(dd,"operateType")) + '</p>' +
                                    '</div>' +
                                    '<div class="col-xs-2">' +
                                    '<p class="form-control-static">' + _getValue(dd,"operateTime") + '</p>' +
                                    '</div>' +
                                    '<div class="col-xs-10 col-xs-offset-2">' +
                                    '<p class="form-control-static">' + _getValue(dd,"opinion") + '</p>' +
                                    '</div>' +
                                    '</div>'
                                );
                            });
                        }
                    }
                    //call ready function
                    if(typeof _options.ready == "function"){
                        _options.ready();
                    }
                }else{
                    if(_flowInsts && _flowInsts.length>1){
                        _selectInst();
                    }else if(_currentTasks && _currentTasks.length>1){
                        _selectTask()
                    }else{
                        alert("流程初始化失败，系统中无相关流程信息");
                    }
                }
            }
        });
    }

    //初始化流程数据
    function _initData(flowData){
        //初始化到全局变量
        $.each(flowData, function(k){
            eval("_" + k + "=flowData." + k);
        });
        //默认值处理
        if(_flowBase && _flowBase.id && _flowNodes && _flowNodes.length){
            //flow _ids
            _setIds("flowDefId", _flowBase.id);
            if(!_getFirstNode()) _setFirstNode(_flowNodes[0].id);
            if(_flowInst && _flowInst.actInstId){
                _setIds("instId", _flowInst.actInstId);
                _isNewFlow = false;
            }else{
                _setIds("nodeId", _getFirstNode());
            }
            //流程环节
            _oFlowNodes = {};
            $.each(_flowNodes, function(i,o){ _oFlowNodes[o.id] = o; });
            //环节按钮
            _oNodeButtons = {};
            if(_nodeButtons && _nodeButtons.length){
                $.each(_nodeButtons, function(i,o){ _oNodeButtons[o.key] = o; });
                //$.extend(true, _buttons, _oNodeButtons);
                $.each(_oNodeButtons,function(i,o){
                    if(!_buttons[i]){
                        _buttons[i] = o;
                    }else{
                        $.each(o,function(n,v){
                            if(v) _buttons[i][n] = v;
                        });
                    }
                });
            }else{
                if(_isNewFlow || _isFirstNode()){
                    //$.extend(_oNodeButtons, _newFlowButtons);
                    $.each(_buttons_new,function(i,k){
                        _oNodeButtons[k] = _buttons[k];
                    });
                }
            }
            $.each(_buttons_def,function(i,k){
                _oNodeButtons[k] = _buttons[k];
            });
            //有授权，显示“收回授权"
            if(_authorizedTasks && _authorizedTasks.length){
                if(!_oNodeButtons["reauthorize"]){
                    //$.extend(_oNodeButtons, _reauthorizeButton);
                    _oNodeButtons["reauthorize"] = _buttons["reauthorize"];
                }
            }else{
                _oNodeButtons["reauthorize"] = false;
            }
            if(_isTaskPool && !_isClaimedTask){
                //$.extend(_oNodeButtons, _claimButton);
                _oNodeButtons["claim"] = _buttons["claim"];
            }else{
                _oNodeButtons["claim"] = false;
            }
            if(!_isTaskPool || !_isClaimedTask){
                _oNodeButtons["backpool"] = false;
            }
            if(_currentTask && _currentTask.id){
                _setIds("nodeId", _currentTask.nodeId);
                _setIds("executionId",_currentTask.actExecutionId);
                //TODO  权限约束
                if(_currentTask.lastOperateType=="authorize"){
                    _oNodeButtons["authorize"] = false; //授权任务，不允许再授权
                    _oNodeButtons["handover"] = false;  //授权任务，不允许再转办
                }else if(_currentTask.lastOperateType=="handover"){
                    _oNodeButtons["authorize"] = false; //转办任务，不允许再授权
                    _oNodeButtons["handover"] = false;  //转办任务，不允许再转办
                }else if(_currentTask.lastOperateType=="append"){
                    _oNodeButtons["append"] = false;    //加签任务，不允许再加签
                }
                if(_currentTask.classify==_classify_hangup){
                    _oNodeButtons["hangup"] = false;
                }
            }
            //$.extend(_oNodeButtons, _buttons);
            //流程默认消息
            _initDefMessages();
            //初始化成功
            return true;
        }else{
            return false;
        }
    }

    //当多个实例记录时，显示选择列表
    function _selectInst(){
        var $list = $("<div class='haf-list'></div>");
        $.each(_flowInsts, function(i, inst){
            var link = "<a href='"
                + CONTEXT_PATH + inst.formId + (inst.formId.indexOf("?") ? "&" : "?") + "instId=" + inst.id + "&dataId=" + inst.dataId
                + "' target='_self'>"
                + "提交人：" + _getValue(inst,"createrName") + "，"
                + "开始时间：" + _getValue(inst,"beginTime") + "，"
                + "结束时间：" + _getValue(inst,"endTime")
                +"</a>";
            $list.append("<div class='haf-list-item'>" + link + "</div>");
        });
        HAF.dialog({
            title: "当前数据在系统中存在多次审批记录，请选择查看",
            content: $list
        });
    }

    //当多个实例记录时，显示选择列表
    function _selectTask(){
        var $list = $("<div class='haf-list'></div>");
        $.each(_currentTasks, function(i, task){
            var link = "<a href='" + _urls.task_redirect + "?taskUserId=" + task.id + "' target='_self'>";
            link += "【" + task.nodeName + "】【" + task.beginTime + "】";
            if(task.authorized && task.ownerId!=task.assignerId){
                link += "【" + task.ownerName + "（授权）】";
            }
            link += "</a>";
            $list.append("<div class='haf-list-item'>" + link + "</div>");
        });
        HAF.dialog({
            title: "当前流程中有多个需要您处理的任务，请选择处理",
            content: $list
        });
    }

    //跳转到指定环节
    function _jumpTo(node){
        var nextNode = typeof node=="object" ? node : _oFlowNodes[node];
        if(nextNode){
            _disableButtons();  //disabled button
            _setContext({targetNodeId: nextNode.id});
            $.get(
                _urls.get_node_user,
                { flowKeyId: _getIds("flowKeyId"), targetNodeId: nextNode.id, instId: _getIds("instId") },
                function(persons){
                    nextNode.assigners = persons;
                    _flowConfirm({
                        nextNodes: [nextNode],
                        requires: {nextPerson: false}
                    });
                }
            );
        }else{
            alert("未获取到驳回环节的配置信息");
        }
    }
    //加签操作
    function _appendTo(appendType){
        HAF.dialog("selectPsn",{
            //singleSelect: true,
            //deptLoad: _getCurrentDept(),
            success: function(persons){
                var personName = HAF.getKeyValue(persons,"name",",");
                var personUsername = HAF.getKeyValue(persons,"sysUsername",",");
                _setContext({appendAuthors: personUsername, appendType: appendType});
                _flowConfirm({
                    displays: {nextNodeRow: true},
                    defaults: {nextPersonName: personName, nextPersonUsername: personUsername, operateName: _appendTypes[appendType]}
                });
            }
        });
    }
    //
    function _classifyTo(className){
        HAF.ajax({
            url: _urls.classify,
            data: {taskUserId: _getIds("taskUserId"),classify: className},
            success: function(data){
                if(data && data.success){
                    alert("分类标记完成");
                }
            }
        });
    }
    //收回任务授权
    function _reauthorizeTask(taskUserId, text){
        var text = text ? "【" + text + "】" : "";
        if(confirm("确定【" + _getButtonText() + "】" + text + "？")){
            _setContext("taskUserId", taskUserId);
            _commit();
        }
    }
    //
    var _commands = {
        quit: function($e){
            if(confirm("确定【" + _getButtonText() + "】页面？（请注意保存您的修改记录）")){
                HAF.closeWindow();
            }
        },
        save: function($e){
            if(confirm("确定【" + _getButtonText() + "】？")){
                _commit();
            }
        },
        submit: function($e){
            _disableButtons();  //disabled button
            _commit();
        },
        reject: function($e){
            var buttonValue = _getButtonValue();
            var nodeList = [];
            if(buttonValue=="[toPreNode]"){
                nodeList.push(_flowHiNodes[_flowHiNodes.length-1]);
            }else if(buttonValue=="[toAnyNode]"){
                nodeList = _flowHiNodes;
            }else if(!buttonValue || buttonValue=="[toStart]"){
                nodeList.push(_flowNodes[0]);
            }else{
                nodeList = buttonValue.split(",");
            }
            if(nodeList.length==1){
                _jumpTo(nodeList[0]);
            }else if(nodeList.length>1){
                //初始化下拉菜单
                if(!_reject_menu_init && $e!=null){
                    var $menu = $('<ul class="dropdown-menu"></ul>');
                    $e.addClass('dropdown-toggle').after($menu);
                    $menu.empty();
                    //驳回选关
                    $.each(nodeList, function(i,id){
                        $menu.append("<li haf-flow-action='"+id+"'><a href='javascript:void(0)'><span>到："+_oFlowNodes[id].name+"</span></a></li>");
                    });
                    //下拉选项事件
                    $menu.find("li").click(function(){
                        _jumpTo($(this).attr("haf-flow-action"));
                    });
                    //避免事件重复注册
                    _reject_menu_init = true;
                    //注册下拉属性
                    $e.attr("data-toggle","dropdown");
                }
            }
        },
        append: function($e){
            var buttonValue = _getButtonValue() || "before;current;after";
            var appendTypes = buttonValue.split(";")
            if(appendTypes.length==1){
                _appendTo(appendTypes[0]);
            }else{
                //初始化下拉菜单
                if(!_append_menu_init && $e){
                    var $menu = $('<ul class="dropdown-menu"></ul>');
                    $e.addClass('dropdown-toggle').after($menu);
                    $menu.empty();
                    $.each(appendTypes, function(i,v){
                        //haf-flow-action="' + v + '"
                        var $li = $('<li><a href="javascript:void(0)"><span>' + _appendTypes[v] + '</span></a></li>');
                        $menu.append($li);
                        $li.click(function(){ _appendTo(v); });
                    });
                    //避免事件重复注册
                    _append_menu_init = true;
                    //注册下拉属性
                    $e.attr("data-toggle","dropdown");
                }
            }
        },
        handover: function($e){
            HAF.dialog("selectPsn",{
                singleSelect: true,
                //deptLoad: _getCurrentDept(),
                success: function(persons){
                    var personName = HAF.getKeyValue(persons,"name",",");
                    var personUsername = HAF.getKeyValue(persons,"sysUsername",",");
                    _setContext({newAssigner: personUsername});
                    _flowConfirm({
                        displays: {nextNodeRow:true},
                        defaults: {nextPersonName: personName, nextPersonUsername: personUsername}
                    });
                }
            });
        },
        revoke: function($e){
            if(confirm("确定【" + _getButtonText() + "】")){
                _commit();
            }
        },
        notify: function($e){
            HAF.dialog("selectPsn",{
                //deptLoad: _getCurrentDept(),
                success: function(persons){
                    var personName = HAF.getKeyValue(persons,"name",",");
                    var personUsername = HAF.getKeyValue(persons,"sysUsername",",");
                    _flowConfirm({
                        displays: {flowCommentRow:true,nextNodeRow:true},
                        defaults: {nextPersonName: personName, nextPersonUsername: personUsername}
                    });
                }
            });
        },
        deliver: function($e){
            HAF.dialog("selectPsn",{
                //deptLoad: _getCurrentDept(),
                success: function(persons){
                    var personName = HAF.getKeyValue(persons,"name",",");
                    var personUsername = HAF.getKeyValue(persons,"sysUsername",",");
                    _flowConfirm({
                        displays: {flowCommentRow:true,nextNodeRow:true},
                        defaults: {nextPersonName: personName, nextPersonUsername: personUsername}
                    });
                }
            });
        },
        authorize: function($e){
            HAF.dialog("selectPsn",{
                //deptLoad: _getCurrentDept(),
                singleSelect: true,
                success: function(persons){
                    var personName = HAF.getKeyValue(persons,"name",",");
                    var personUsername = HAF.getKeyValue(persons,"sysUsername",",");
                    _setContext({authorAssigner:personUsername});
                    _flowConfirm({
                        displays: {flowCommentRow:true,nextNodeRow:true},
                        defaults: {nextPersonName:personName,nextPersonUsername:personUsername}
                    });
                }
            });
        },
        reauthorize: function($e){
            if(_currentTask){
                var text = _currentTask.assignerName + "[" + _currentTask.nodeName + "]";
                _reauthorizeTask(_currentTask.id, text);
            }else{
                if(!_reauthorize_menu_init && $e){
                    var $menu = $('<ul class="dropdown-menu"></ul>');
                    $e.addClass('dropdown-toggle').after($menu);
                    $menu.empty();
                    $.each(_authorizedTasks,function(i,task){
                        var text = task.assignerName + "[" + task.nodeName + "]";
                        var $li = $("<li><a href='javascript:void(0)'><span>" + text + "</span></a></li>");
                        $menu.append($li);
                        $li.click(function(){
                            _reauthorizeTask(task.id, text);
                        });
                    });
                    //避免事件重复注册
                    _reauthorize_menu_init = true;
                    //注册下拉属性
                    $e.attr("data-toggle","dropdown");
                }
            }
        },
        claim: function($e){
            if(confirm("确定【" + _getButtonText() + "】？")){
                HAF.ajax({
                    url: _urls.single_claim,
                    data: {taskUserId: _getIds("taskUserId")},
                    success: function(data){
                        if(data && data.success){
                            alert("认领成功，流程将重新加载，请稍后");
                            self.location.reload();
                        }
                    }
                });
            }
        },
        hangup: function($e){
            if(confirm("确定【" + _getButtonText() + "】？")){
                _setContext("classify",_classify_hangup);
                _commit();
            }
        },
        backpool: function($e){
            if(confirm("确定【" + _getButtonText() + "】？")){
                _commit();
            }
        },
        classify: function($e){
            if(!_classify_menu_init && $e){
                var $menu = $('<ul class="dropdown-menu"></ul>');
                $e.addClass('dropdown-toggle').after($menu);
                $menu.empty();
                $.each(_classifyList,function(i,c){
                    var $li = $("<li><a href='javascript:void(0)'><span>" + c.text + "</span></a></li>");
                    $menu.append($li);
                    $li.click(function(){
                        _classifyTo(c.name);
                    });
                });
                //避免事件重复注册
                _classify_menu_init = true;
                //注册下拉属性
                $e.attr("data-toggle","dropdown");
            }
        },
        communicate: function($e){
            _communicate();
        },
        jump: function($e){
            if(!_jump_menu_init && $e){
                var buttonValue = _getButtonValue()||"";
                var jumpNodes = buttonValue.split(";");
                var $menu = $('<ul class="dropdown-menu"></ul>');
                $e.addClass('dropdown-toggle').after($menu);
                $menu.empty();
                if(buttonValue && jumpNodes.length>0){
                    $.each(jumpNodes,function(i,nodeId){
                        var $li = $("<li><a href='javascript:void(0)'><span>到：" + _getNodeValue(nodeId,"name") + "</span></a></li>");
                        $menu.append($li);
                        $li.click(function(){ _jumpTo(nodeId); });
                    });
                }else{
                    $.each(_oFlowNodes, function(id, node){
                        //haf-flow-action='"+id+"'
                        var $li = $("<li><a href='javascript:void(0)'><span>到："+node.name+"</span></a></li>");
                        $menu.append($li);
                        $li.click(function(){ _jumpTo(id); });
                    });
                }
                //避免事件重复注册
                _jump_menu_init = true;
                //注册下拉属性
                $e.attr("data-toggle","dropdown");
            }
        },
        replace: function($e){
            HAF.dialog("selectPsn",{
                //deptLoad: _getCurrentDept(),
                singleSelect: true,
                success: function(persons){
                    var personName = HAF.getKeyValue(persons,"name",",");
                    var personUsername = HAF.getKeyValue(persons,"sysUsername",",");
                    _setContext({newAssigner: personUsername});
                    _flowConfirm({
                        displays: {flowCommentRow:true,nextNodeRow:true},
                        defaults: {nextPersonName:personName,nextPersonUsername:personUsername}
                    });
                }
            });
        },
        stop: function($e){
            if(confirm("确定【" + _getButtonText() + "】？")){
                _commit();
            }
        },
        manage: function($e){
            //TODO get Authors and Readers List,Show Manage Dialog
        },
        print: function($e){
            alert("为了更好的打印效果，建议使用浏览器的【文件】【打印预览】功能");
            window.print();
        },
        view: function($e){
            //window.open(_options.viewUrl + "?" + HAF.urlSerialize(_ids));
            HAF.dialog({
                key: 'flow_view',
                title: '流程查看',
                content: '',
                forceInit: false,
                width: '800px',
                backdrop: true,
                ready: function($dialog){
                    var html = '<div class="flow-hi-view">' +
                        '<ul class="nav nav-tabs">' +
                        '<li class="active"><a href="#_flow_view_img" flow-hi-type="img">流程图</a></li>' +
                        '<li><a href="#_flow_hi_sub_flow" flow-hi-type="sub">子流程</a></li>' +
                        '<li><a href="#_flow_hi_node" flow-hi-type="node">流转记录</a></li>' +
                        '<li><a href="#_flow_hi_action" flow-hi-type="action">操作记录</a></li>' +
                        '<li><a href="#_flow_hi_task" flow-hi-type="task">任务记录</a></li>' +
                        '<li><a href="#_flow_hi_comm" flow-hi-type="comm">沟通记录</a></li>' +
                        '<li><a href="#_flow_hi_comment" flow-hi-type="comment">批示意见</a></li>' +
                        '</ul>' +
                        '<div class="tab-content">' +
                        '<div class="tab-pane active" id="_flow_view_img"></div>' +
                        '<div class="tab-pane" id="_flow_hi_sub_flow"></div>' +
                        '<div class="tab-pane" id="_flow_hi_node"></div>' +
                        '<div class="tab-pane" id="_flow_hi_action"></div>' +
                        '<div class="tab-pane" id="_flow_hi_task"></div>' +
                        '<div class="tab-pane" id="_flow_hi_comm"></div>' +
                        '<div class="tab-pane" id="_flow_hi_comment"></div>' +
                        '</div>' +
                        '</div>';
                    $dialog.find(".modal-body").html(html);
                    $dialog.find(".nav li a").attr("data-toggle","tab").click(function(){
                        var pId = $(this).attr("href");
                        var $pane = $dialog.find(pId);
                        var hiType = $(this).attr("flow-hi-type");
                        if($pane.html()==""){
                            var $loading = $('<div class="loading-img"></div>');
                            $loading.appendTo($pane);
                            if(hiType=="img"){
                                $('<img src="' + _urls.view + "?" + HAF.urlSerialize(_ids) + '" style="display:none">').load(function(){
                                    $loading.remove();
                                    $pane.css("text-align","center").find("img").show();
                                }).appendTo($pane);
                            }else if(hiType=="comment"){
                                $loading.remove();
                                var $table = $('<table class="table flow-hi-table"></table>');
                                $table.append('<thead>' +
                                    '<tr><td>处理人</td><td>处理环节</td><td>处理类型</td><td>处理时间</td></tr>' +
                                    '</thead>'
                                );
                                $.each(_flowHiComments, function(i,dd){
                                    $table.append('<tr class="flow-user-row">' +
                                        '<td>' + _getValue(dd,"operaterName") + '</td>' +
                                        '<td>' + _getValue(dd,"nodeName") + '</td>' +
                                        '<td>' + _getButtonText(_getValue(dd,"operateType")) + '</td>' +
                                        '<td>' + _getValue(dd,"operateTime") + '</td>' +
                                        '</tr>' +
                                        '<tr class="flow-comment-row"><td colspan="4">' + _getValue(dd,"opinion") + '</td></tr>'
                                    );
                                });
                                $table.appendTo($pane);
                            }else if(!_isNewFlow){
                                $.get(_urls.get_history, {instId: _getIds("instId"), hiType: hiType}, function(data){
                                    $loading.remove();
                                    if(data && data.length){
                                        var $table = $('<table class="table flow-hi-table"></table>');
                                        if(hiType=="node"){
                                            $table.append('<thead>' +
                                                '<tr><td>流程环节</td><td>环节类型</td><td>开始时间</td><td>结束时间</td></tr>' +
                                                '</thead>'
                                            );
                                            $.each(data, function(i,dd){
                                                if(_getValue(dd,"nodeName")){
                                                    $table.append('<tr class="">' +
                                                        '<td>' + _getValue(dd,"nodeName") + '</td>' +
                                                        '<td>' + _getValue(dd,"nodeType") + '</td>' +
                                                        '<td>' + _getValue(dd,"nodeBeginTime") + '</td>' +
                                                        '<td>' + _getValue(dd,"nodeEndTime") + '</td>' +
                                                        '</tr>'
                                                    );
                                                }
                                            });
                                        }else if(hiType=="action"){
                                            $table.append('<thead>' +
                                                '<tr><td>操作时间</td><td>操作人</td><td>操作类型</td><td>操作环节</td><td>流向环节</td></tr>' +
                                                '</thead>'
                                            );
                                            $.each(data, function(i,dd){
                                                $table.append('<tr>' +
                                                    '<td>' + _getValue(dd,"operateTime") + '</td>' +
                                                    '<td>' + _getValue(dd,"operaterName") + '</td>' +
                                                    '<td>' + _getButtonText(_getValue(dd,"operateType")||"-") + '</td>' +
                                                    '<td>' + _getValue(dd,"nodeName") + '</td>' +
                                                    '<td>' + _getValue(dd,"flowNodeName") + '</td>' +
                                                    '</tr>'
                                                );
                                            });
                                        }else if(hiType=="task"){
                                            $table.append('<thead>' +
                                                '<tr><td>任务环节</td><td>开始时间</td><td>完成时间</td><td>预定处理人</td>' +
                                                '<td>实际处理人</td></tr>' +
                                                '</thead>'
                                            );
                                            $.each(data, function(i,dd){
                                                $table.append('<tr>' +
                                                    '<td>' + _getValue(dd,"nodeName") + '</td>' +
                                                    '<td>' + _getValue(dd,"beginTime") + '</td>' +
                                                    '<td>' + _getValue(dd,"endTime") + '</td>' +
                                                    '<td>' + _getValue(dd,"preAssignerName") + '</td>' +
                                                    '<td>' + _getValue(dd,"realAssignerName") + '</td>' +
                                                    '</tr>'
                                                );
                                            });
                                        }else if(hiType=="comm"){
                                            $table.append('<thead>' +
                                                '<tr><td>处理人</td><td>处理环节</td><td>操作类型</td><td>处理时间</td></tr>' +
                                                '</thead>'
                                            );
                                            $.each(data, function(i,dd){
                                                var nodeName = _getNodeValue([dd.refKey],"name");
                                                $table.append('<tr class="flow-user-row">' +
                                                    '<td>' + _getValue(dd,"creatorName") + '</td>' +
                                                    '<td>' + nodeName + '</td><td>发起</td>' +
                                                    '<td>' + _getValue(dd,"createDate") + '</td>' +
                                                    '</tr>' +
                                                    '<tr class="flow-comment-row"><td colspan="4">' + _getValue(dd,"content") + '</td></tr>'
                                                );
                                                if(_getValue(dd,"replyDate")){
                                                    $table.append('<tr class="flow-user-row">' +
                                                        '<td>' + _getValue(dd,"replierName") + '</td>' +
                                                        '<td>' + nodeName + '</td><td>答复</td>' +
                                                        '<td>' + _getValue(dd,"replyDate") + '</td>' +
                                                        '</tr>' +
                                                        '<tr class="flow-comment-row"><td colspan="4">' + _getValue(dd,"replyContent") + '</td></tr>'
                                                    );
                                                }
                                            });
                                        }
                                        $pane.append($table);
                                    }else{
                                        $pane.html("<p>未查询到相关记录</p>");
                                    }
                                });
                            }else{
                                $pane.html("<p>未查询到相关记录</p>");
                            }
                        }
                    }).first().click();
                }
            });
        }
    }

    //调用 _commands {} 中的方法
    function _command($e, cmd){
        var $e, cmd;
        for(var i=0;i<arguments.length;i++){
            if(typeof arguments[i]=="string") cmd = arguments[i];
            else $e = arguments[i];
        }
        if(!$e) $e = $(".haf-flow-actions .haf-flow-button[haf-flow-action="+cmd+"]");
        var func = _commands[cmd];
        if(typeof func == "function"){
            _resetContext(cmd);
            func($e);
        }
    }

    //===流程提交操作确认 flow confirm =================
    function _flowConfirm(options){
        var nextNodes = [/*{id: "", name: "", type: "", replacePerson: 0, assigners: null}*/];
        var defaults = {operateName:"",flowComment: "",flowMessage:""};
        var requires = {flowComment: true, flowMessage: true, nextPerson: true};
        var displays = {nextPersonRow:false, flowCommentRow:false, flowMessageRow:false, commentsAddRow:true};
        if(options){
            if(options.nextNodes) nextNodes = options.nextNodes;    //$.extend(nextNodes, options.nextNodes);
            if(options.defaults) $.extend(defaults, options.defaults);
            if(options.requires) $.extend(requires, options.requires);
            if(options.displays) $.extend(displays, options.displays);
        }
        if(!defaults.operateName) defaults.operateName = _getButtonText();
        if(!defaults.flowMessage) defaults.flowMessage = _getFlowMessage();
        displays.commentsAddRow = !displays.flowCommentRow;
        if(nextNodes[0] && _isEndNode(nextNodes[0].id)){
            displays.nextPersonRow = true;
            defaults.nextNodeName = "结束";
        }
        //
        _setNextNodes(nextNodes);
        //===dialog=========
        HAF.dialog({
            key: "flow_cf",
            title: "请确认操作",
            content: HAF.Dialog.HTMLTemplate.flowConfirm,
            width: "620px",
            ready: function($dialog){
                $dialog.find("[haf-comp-id='nextNodeRow']:gt(0)").remove();
                $dialog.find("[haf-comp-id='nextPersonRow']:gt(0)").remove();
                $dialog.find("[haf-comp-id]").each(function(){
                    var id = $(this).attr("haf-comp-id");
                    if(displays[id]){
                        requires[id] = false;
                        $(this).hide();
                    }else{
                        $(this).show();
                    }
                    if($(this).is("input:text,textarea")){
                        $(this).val(defaults[id]||"");
                    }
                });
                //next nodes
                if(!displays.nextPersonRow){
                    var $nRow = null; //node row
                    var $pRow = null; //psn row
                    $.each(nextNodes, function(i,nextNode){
                        if($nRow){
                            $nRow = $nRow.clone().insertAfter($pRow);
                            $pRow = $pRow.clone().insertAfter($nRow);
                        }else{
                            $nRow = $dialog.find("div[haf-comp-id='nextNodeRow']");
                            $pRow = $dialog.find("div[haf-comp-id='nextPersonRow']");
                        }
                        $nRow.find("label span").html("["+(i+1)+"]");
                        $pRow.find("label span").html("["+(i+1)+"]");
                        //
                        $nRow.find("input:first").val(nextNode.name+(nextNode.groupTask?"[任务池]":""));
                        var nextPersonUsername, nextPersonName;
                        if(nextNode.groupTask){
                            nextNode.replacePerson = false;
                        }
                        if(nextNode.assigners && nextNode.assigners.length){
                            nextPersonUsername = HAF.getKeyValue(nextNode.assigners,"sysUsername",",");
                            nextPersonName = HAF.getKeyValue(nextNode.assigners,"name",",");
                            if(nextPersonName.replace(/,| /g,"")=="") nextPersonName = nextPersonUsername;
                            $pRow.find("input:first").val(nextNode.groupTask?"[系统]":nextPersonName);
                            $nRow.show();
                            $pRow.show();
                        }else{
                            if(_isEndNode(nextNode.id)){
                                nextNode.replacePerson = 0;
                                $nRow.hide();
                                $pRow.hide();
                            }else{
                                nextNode.replacePerson = 1;
                                $nRow.show();
                                $pRow.show();
                            }
                        }
                        if(nextNodes.length<2){
                            if(!$nRow){
                                $nRow = $dialog.find("div[haf-comp-id='nextNodeRow']");
                                $pRow = $dialog.find("div[haf-comp-id='nextPersonRow']");
                            }
                            $nRow.find("label span").html("");
                            $pRow.find("label span").html("");
                        }
                        //
                        if(nextNode.replacePerson){
                            $pRow.find("input[haf-comp-id='nextPersonName']").parent().removeClass().addClass("col-xs-6");
                            $pRow.find("button[haf-comp-id='authorSelector']").unbind("click").click(function(){
                                var $inputs = $(this).parent().prev();
                                HAF.dialog("selectPsn",{
                                    backdrop: false,
                                    //deptLoad: _getCurrentDept(),
                                    success: function(persons){
                                        var personNames = HAF.getKeyValue(persons, "name", ",");
                                        var personUsername = HAF.getKeyValue(persons, "sysUsername", ",");
                                        $inputs.find("input[haf-comp-id='nextPersonName']").val(personNames);
                                        $inputs.find("input[haf-comp-id='nextPersonUsername']").val(personUsername);
                                    }
                                });
                            }).parent().show();
                        }else{
                            $pRow.find("button[haf-comp-id='authorSelector']").parent().hide();
                            $pRow.find("input[haf-comp-id='nextPersonName']").parent().removeClass().addClass("col-xs-8");
                        }
                    });
                    if(!nextNodes.length){
                        $dialog.find("button[haf-comp-id='authorSelector']").parent().hide();
                        $dialog.find("input[haf-comp-id='nextPersonName']").parent().removeClass().addClass("col-xs-8");
                    }
                }
                //显示批注标签
                $dialog.find("div[haf-comp-id='commentsAdd'] span").click(function(){
                    $dialog.find("div[haf-comp-id='commentsAddRow']").hide();
                    $dialog.find("div[haf-comp-id='flowCommentRow']").show();
                });
                //预定的批注选择
                $dialog.find("div[haf-comp-id='tempComments'] span").click(function(){
                    $dialog.find("textarea[haf-comp-id='flowComment']").val($(this).text());
                });
            },
            success: function($dialog){
                var _data = {};
                var _valid = true;
                if(!displays.nextPersonRow){
                    $dialog.find("input[haf-comp-id='nextPersonUsername']").each(function(i,e){
                        var v = e.value;
                        var v0 = $(e).prev().val();
                        if(!v0 && requires.nextPerson){
                            alert("请选择【"+$(this).parent().prev().text()+"】");
                            _valid = false;
                            return false;
                        }else if(v){
                            _setAssigner(nextNodes[i].id, v);
                        }
                    });
                }
                if(!_valid){ return false;}
                //flowComment
                var $text = $dialog.find("textarea[haf-comp-id='flowComment']");
                var v = $text.val();
                if(!v && !displays.flowCommentRow && requires.flowComment){
                    alert("请填写【"+$text.parent().prev().text()+"】");
                    return false;
                }
                _setContext("flowComment", v);
                //flowMessage
                $text = $dialog.find("input[haf-comp-id='flowMessage']");
                v = $text.val();
                if(!v && !displays.flowMessageRow && requires.flowMessage){
                    alert("请填写【"+$text.parent().prev().text()+"】");
                    return false;
                }
                _setContext("flowMessage", v);
                //close dialog
                $dialog.modal('toggle');
                //beforeOk
                if(typeof options.beforeOk == "function" && options.beforeOk()===false){
                    return false;
                }
                //-----------------------
                // execute flow
                if(_getOperateType()=="submit"){
                    _execute();
                }else{
                    _commit();
                }
                //禁用HAF.dialog本身的‘toggle’
                return false;
            }
        });
    }

    function _communicate(){
        HAF.dialog({
            key: "comm_cr",
            title: "在线沟通",
            content: HAF.Dialog.HTMLTemplate.createComm,
            width: "620px",
            ready: function($dialog){
                $dialog.find("input[haf-comp-id='subject']").val("关于“"+_flowBase.name+"”，请回复");
                $dialog.find("input[haf-comp-id='refId']").val(_getIds("instId"));
                $dialog.find("input[haf-comp-id='refKey']").val(_getIds("nodeId"));
                $dialog.find("button[haf-comp-id='replierSelector']").click(function(){
                    var $inputs = $(this).parent().prev();
                    HAF.dialog("selectPsn",{
                        backdrop: false,
                        //deptLoad: _getCurrentDept(),
                        singleSelect: true,
                        success: function(persons){
                            var personNames = HAF.getKeyValue(persons, "name", ",");
                            var personUsername = HAF.getKeyValue(persons, "sysUsername", ",");
                            $inputs.find("input[haf-comp-id='replierName']").val(personNames);
                            $inputs.find("input[haf-comp-id='replierId']").val(personUsername);
                        }
                    });
                });
            },
            success: function($dialog){
                var _data = {};
                var _valid = true;
                $dialog.find("input[haf-comp-id],textarea[haf-comp-id]").each(function(i,e){
                    var v = $(this).val();
                    if(!v && $(this).is(":visible")){
                        alert("请选择或者填写【"+$(this).parent().prev().text()+"】");
                        _valid = false;
                        return false;
                    }
                    _data[$(this).attr("haf-comp-id")] = v;
                });
                if(!_valid){ return false;}
                //close dialog
                $dialog.modal('toggle');
                //submit
                HAF.ajax({
                    url: _urls.create_comm,
                    data: _data,
                    success: function(data){
                        if(data.success){
                            alert("沟通信息已发送，请等待回复");
                        }
                    }
                });
                //禁用HAF.dialog本身的‘toggle’
                return false;
            }
        });
    }

    /** commit ****
     *  流程执行
     */
    //获取流程相关数据
    function _parseProcessVar(){
        if(typeof _options.parseProcessVar=="function"){
            var d = _options.parseProcessVar();
            if(typeof d=="object") HAF.setKeyValue(_flowContext.processVar, d);
        }
    }
    function _parseSubProcessVar(){
        if(typeof _options.parseSubProcessVar=="function"){
            var d = _options.parseSubProcessVar();
            if(typeof d=="object") HAF.setKeyValue(_flowContext.subProcessVar, d);
        }
    }
    function _parseBizData(){
        if(typeof _options.parseBizData=="function"){
            var d = _options.parseBizData();
            if(typeof d=="object") HAF.setKeyValue(_bizData, d);
        }
    }
    function _parseAllData(){
        _parseProcessVar();
        _parseSubProcessVar();
        _parseBizData();
    }
    //执行相关验证
    function _commit(){
        var cmd = _getOperateType();
        var onFnName = "on" + cmd.replace(/^[a-z]/,function(s){return s.toUpperCase();});
        //function onXxxx()
        if(typeof _options[onFnName]=="function"){
            var r = _options[onFnName]();
            if(typeof r=="boolean" && r==false) return false;
        }
        //
        _parseAllData();
        //
        if(cmd=="submit"){
            $.post(_urls.get_next_node, _flowContext, function(nextNodes){
                _flowConfirm({
                    nextNodes: nextNodes,
                    requires: {flowComment: !_isFirstNode()},
                    displays: {flowCommentRow: _isFirstNode()}
                });
            });
        }else{
            _execute();
        }
    }
    //所有验证结束，流程执行
    function _execute(){
        if(typeof _options.beforeCommit=="function"){
            var r = _options.beforeCommit(_nextNodes);
            if(typeof r=="boolean" && r==false) return false;
        }
        HAF.ajax({
            url: _urls.submit,
            contentType : 'application/json',
            data: HAF.stringify({flowContext: _flowContext, bizData: _bizData}),
            success: function(data){
                if(data && data.success){
                    alert("流程【" + _getButtonText() + "】成功！");
                    if(typeof _options.afterCommit=="function"){
                        _options.afterCommit();
                    }
                    HAF.closeWindow()
                }else{
                    alert("流程操作失败！错误原因："+data.error);
                }
            }
        });
    }

    //==========================================================
    $.extend(window.HAF.Flow, {
        init: function(options){
            _init(options);
        },
        command: function(cmd){
            _command(cmd);
        },
        commit: function(){
            _commit();
        },
        initContext: function(){
            _initContext();
        },
        setContext: function(k,v){
            _setContext(k,v);
        },
        resetContext: function(){
            _resetContext();
        },
        //
        isStartNode: function(nodeId){
            return _isStartNode(nodeId);
        },
        isFirstNode: function(nodeId){
            return _isFirstNode(nodeId);
        },
        isEndNode: function(nodeId){
            return _isEndNode(nodeId);
        },
        //public getter, setter
        setIds: function(k,v){
            _setIds(k,v);
        },
        getIds: function(k){
            return _getIds(k);
        },
        getOperateType: function(){
            return _getOperateType();
        },
        getButtonText: function(operateType){
            return _getButtonText(operateType);
        },
        setOperateType: function(operateType){
            _setOperateType(operateType);
        },
        getCurrentPerson: function(){
            return _getCurrentPerson();
        },
        getCurrentDate: function(){
            return _getCurrentDate();
        },
        getCurrentDept: function(){
            return _getCurrentDept();
        },
        getCurrentNode: function(){
            return _getCurrentNode();
        },
        getFlowBase: function(){
            return _getFlowBase();
        },
        getFlowNodes: function(){
            return _getFlowNodes();
        },
        setProcessVar: function(k,v){
            _setProcessVar(k,v);
        },
        setSubProcessVar: function(k,v){
            _setSubProcessVar(k,v);
        },
        getNextNodes: function(){
            return _getNextNodes();
        },
        setAssigner: function(n,a){
            _setAssigner(n,a);
        },
        setBizData: function(k,v){
            _setBizData(k,v);
        }
    });

})(window, undefined, jQuery);/**
 * HAF 表单处理
 * 扩展HAF JS库方法
 * HAF.Form
 * 1.attachment manager
 */
(function(window, undefined, $){

    if(!window.HAF) return;
    if(!HAF.Form) HAF.Form = {};

    /**
     * 附件上传控件
     */
    var _file_uploader = "";

    function _fileUpload(success, error, selectType){
        var success = success || function(data){

        };
        var error = error || function(msg){
            alert(msg);
        };

        var singleSelect = selectType && selectType=="single";
        if(!_file_uploader) _file_uploader = "_" + new Date().getTime();

        var upload_url = HAF.basePath() + "/oa/service/attach/upload.do";
        var download_url = HAF.basePath() + "/oa/service/attach/download.do";
        var list_url = HAF.basePath() + "/oa/service/attach/list.do";
        var class_list_url = HAF.basePath() + "/oa/service/attach/classList.do";
        var target = "_haf_upload_iframe" + _file_uploader;

        var html =
        //nav tab
            '<ul class="nav nav-tabs">' +
            '<li class="active"><a href="#upload_tab' + _file_uploader + '" data-toggle="tab">文件上传</a></li>' +
            '<li><a href="#att_list_tab' + _file_uploader + '" data-toggle="tab">我的附件库</a></li>' +
            '</ul>' +
        //tab content begin
            '<div class="tab-content">' +
        //tab1 - 上传控件
            '<div class="tab-pane active" id="upload_tab' + _file_uploader + '">' +
            '<iframe name="' + target + '" style="display:none;"></iframe>' +
            '<form method="post" enctype="multipart/form-data" target="' + target + '" action="' + upload_url + '">' +
            '<div style="width:100%;padding:20px 10px 40px 10px">' +
            '<input type="file" name="file" style="width:50%;float:left">' +
            '<div style="float:right;width:250px;">文件分类：<input type="text" name="classify" style="width:200px;"></div>' +
            '</div>' +
            '</form>' +
            '</div>' +
        //tab2 - 我的附件
            '<div class="tab-pane active" id="att_list_tab' + _file_uploader + '">' +
            '' +
            '</div>' +
        //tab content end
            '</div>';

        HAF.dialog({
            key: _file_uploader,
            title: "附件上传",
            content: html,
            forceInit: true,
            width: "650px",
            ready: function($d){
                /*$d.find("form a.btn").unbind().click(function(){
                    if($d.find("input:file").val()==""){
                        alert("请选择上传的文件");
                    }else{
                        $d.find("form")[0].submit();
                    }
                });*/
                $d.find("iframe").unbind().bind("load",function(){
                    var doc = $d.find("iframe")[0].contentDocument || $d.find("iframe")[0].contentWindow.document;
                    var data;
                    var str = doc.body.innerHTML;
                    if(str.replace(/ |\r|\n/,"")){
                        try {
                            data = eval("("+str+")");
                            if(data){
                                if(data.success){
                                    success(data.data);
                                    $d.modal("toggle");
                                }else{
                                    error(data.msg);
                                }
                            }
                        } catch (e) {
                            error("系统错误，操作失败");
                        }
                    }
                });
                $d.find(".nav-tabs a:first").click(function(){
                    $d.find("#att_classes" + _file_uploader).hide();
                });
                $d.find(".nav-tabs a:last").click(function(){
                    var $list = $("#att_list_tab" + _file_uploader);
                    if($list.html()==""){
                        var str = '<div style="width:100%;height:230px"><table id="att_list' + _file_uploader + '"></table></div>';
                        $list.append(str);
                        str = '<select id="att_classes' + _file_uploader + '"' +
                            ' style="min-width:120px;max-width:200px;position:absolute;right:20px;top:12px;display:none">' +
                            '<option value="">** 类型筛选 **</option>' +
                            '<option value="[NULL]">** [未分类] **</option>' +
                            '</select>'
                        $d.find(".modal-body").append(str);
                        $("#att_list" + _file_uploader).datagrid({
                            fit: true,
                            striped: true,
                            method: "POST",
                            rownumbers:true,
                            pageSize:15,
                            pagination: true,
                            pageList:[5,15,30,50],
                            border: false,
                            fitColumns: true,
                            checkbox: true,
                            singleSelect: singleSelect,
                            url: list_url,
                            //queryParams: {},
                            idField: 'id',
                            columns: [[
                                {field:'id',checkbox:true},
                                {field:'originalName',title:'文件名称',width:200,align:'center'},
                                {field:'createDate',title:'上传时间',width:120,align:'center'},
                                {field:'extType',title:'类型',width:60,align:'center'},
                                {field:'fileSize',title:'大小',width:60,align:'center'},
                                {field:'classify',title:'分类',width:80,align:'center'},
                                {field:'_action',title:'#',width:60,align:'center',formatter:function(value,row){
                                    return "<a target='_blank' href='" + download_url + "?id=" + row.id + "' style='color:#3333CC;'>查看</a>";
                                }}
                            ]]
                        });
                        $.get(
                            class_list_url + "?t" + new Date().getTime(),
                            "",
                            function(data){
                                if(data.length){
                                    var ss = "";
                                    $.each(data,function(i,v){
                                        ss += '<option value="' + v + '">' + v + '</option>';
                                    });
                                    $("#att_classes" + _file_uploader).append(ss);
                                }
                                $("#att_classes" + _file_uploader).unbind().change(function(){
                                    $("#att_list" + _file_uploader).datagrid("load",{classify: $(this).val()});
                                });
                            }
                        );
                    }
                    //
                    $d.find("#att_classes" + _file_uploader).show();
                });
            },
            success: function($d){
                if($d.find(".nav-tabs li:first").is(".active")){
                    if($d.find("#upload_tab" + _file_uploader + " input:file").val()==""){
                        alert("请选择上传的文件");
                    }else{
                        $d.find("#upload_tab" + _file_uploader + " form")[0].submit();
                    }
                    return false;
                }else{
                    var data = $("#att_list" + _file_uploader).datagrid("getSelections");
                    if(data.length){
                        success(data);
                    }else{
                        alert("请选择需要的文件");
                        return false;
                    }
                }
            }
        });
    }

    /**
     * 表单控制
     */
    function _setReadonly(form,filters){
        var $f = $(form), $e;
        if(filters) $e = $f.find(filters);
        else $e = $f.find("input:text,:radio,:checkbox,select,textarea").filter(":visible");
        $e.each(function(){
            if($(this).is(":text,textarea")) this.readOnly = true;
            else this.disabled = true;
        });
    }

    /**
     * 表单数据序列化为json
     */
    function _formToJson(form){
        var data = {};
        var $form = $(form);
        if($form.size()>0){
            $form.find("input:text,input:hidden,select,textarea").filter("[name]:enabled").each(function(){
                data[this.name] = $(this).val();
            });
            $form.find("input:checked").each(function(){
                if($(this).is(":checkbox")){
                    if(!data[this.name]) data[this.name] = [];
                    data[this.name].push($(this).val());
                }else{
                    data[this.name] = $(this).val();
                }
            });
        }
        return data;
    }

    function _setDatepicker(selector){
        var selector = selector || "form .date-input";
        $(selector).attr('readonly','readonly').each(function(){
            var $p = $(this).parent();
            var $g = $('<div class="input-group"></div>');
            $p.append($g).addClass('date-input-group');
            $(this).appendTo($g);
            $g.append('<span class="input-group-addon"><i class="glyphicon glyphicon-calendar"></i></span>');
            $p.datepicker();
        });
    }

    function _setInputSelect(selector){
        var selector = selector || "form .select-input";
        $(selector).hide().each(function(){
            var $target = $(this);
            var id = this.id;
            var $s = false;
            if(id) $s = $("select[haf-comp-for='"+id+"']");
            if(!$s && $target.next().is("select")) $s = $target.next();
            if($s){
                $s.val($target.val()).change(function(){
                    $target.val($(this).val());
                }).show();
            }
        });
    }

    /**
     * 方法扩展到HAF工具类
     */
    $.extend(window.HAF.Form, {
        fileUpload: function(success, error, selectType){
            _fileUpload(success, error, selectType);
        },
        readonly: function(form,filters){
            _setReadonly(form,filters);
        },
        datepicker: function(selector){
            _setDatepicker(selector);
        },
        inputSelect: function(selector){
            _setInputSelect(selector);
        }
    });

})(window, undefined, jQuery);