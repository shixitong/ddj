/**
 * use bootstrap js
 */
(function(window, undefined, $){

    if(!window.HAF) return;
    if(!window.HAF.Dialog) window.HAF.Dialog = {};

    var _url_psn_dept = "/oa/service/comorg/getOrgChildren.do";
    var _url_psn_by_dept = '/oa/service/comorg/getPsnByDept.do';
    var _url_org_select = '/oa/service/comorg/getOrgAllChildren.do';
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
                    rownumbers: true,
                    border: false,
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
    function _selectOrg(_options){

        var options = $.extend({
            backdrop: "static", //模态窗口背景遮罩，false不显示，true点击隐藏，static静态
            rootId: "COM_ROOT", //树默认根节点组织
            orgLoad: "",         //默认加载的组织
            orgTreeQuery:{dptType:"SYB"},    //默认树的查询条件 {bizType:"SN",funType:"",dptType:"SYB"}
            orgListQuery:{},    //默认grid的查询条件 {bizType:"SN",funType:"YX",dptType:"SCB"}
            singleSelect: false //是否单选
        }, _options);

        var selectedData = {};
        var treeUrl = HAF.basePath() + _url_org_select;
        if(!options.orgLoad) options.orgLoad = options.rootId;

        options.orgTreeQuery["orgId"] = options.rootId;
        options.orgListQuery["orgId"] = options.orgLoad;

        var $tree ,$grid ,$selected ,$selectedBtns;
        var treeBtnIndex = -1;

        _modal({
            key: "org_sel",
            title: "组织机构选择",
            content: _HTMLTemplate.selectOrg,
            backdrop: options.backdrop,
            width: "700px",
            ready: function($dialog){

                $tree = $dialog.find("div[haf-comp-id='deptTree'] ul");
                $grid = $dialog.find("div[haf-comp-id='orgList'] table");
                $selected = $dialog.find("div[haf-comp-id='orgSelected']");
                $selectedBtns = $dialog.find("div[haf-comp-id='orgSelectedBtns']");

                //dept tree
                $.get(HAF.basePath() + _url_org_select, options.orgTreeQuery, function(data){
                    var dd = [];
                    $.each(data,function(i,d){
                        dd.push({
                            id: d.id,
                            text: d.name
                        });
                    });
                    $tree.tree({
                        lines: true,
                        data: dd,
                        onSelect: function(node){
                            options.orgListQuery["orgId"] = node.id;
                            $grid.datagrid('load',options.orgListQuery);
                        }
                    });
                });

                //org list
                $grid.datagrid({
                    //fit: true,
                    width:330,
                    height:320,
                    fitColumns: true,
                    striped: true,
                    rownumbers: true,
                    //pagination: true,
                    border: false,
                    singleSelect: options.singleSelect,
                    url: HAF.basePath() + _url_org_select + "?t" + (new Date()).getTime(),
                    queryParams: options.orgListQuery,
                    idField: 'id',
                    columns: [[
                        {field:'id',checkbox:true},
                        {field:'name',title:'名称',width:200,align:'left'},
                        {field:'code',title:'代码',width:60,align:'center'}
                    ]],
                    onSelect: function(rowIndex, rowData){
                        var span = $selected.find("span[dataId='"+rowData.id+"']");
                        if(span.size()==0){
                            if(options.singleSelect){
                                $selected.children().remove();
                                selectedData = {};
                            }
                            $selected.append("<span dataId='"+rowData.id+"'>"+rowData.name+"</span>");
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
                                $selected.append("<span dataId='"+rowData.id+"'>"+rowData.name+"</span>");
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
                    alert("尚未选择任何组织");
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
        selectOrg:
            '<table class="org-select">' +
            '<tr>' +
            '<td>' +
            '<div haf-comp-id="deptTree"><div><ul></ul></div></div>' +
            '</td>' +
            '<td><div haf-comp-id="orgList"><table></table></div></td>' +
            '<td>' +
            '<div haf-comp-id="orgSelected"></div>' +
            '<div haf-comp-id="orgSelectedBtns">' +
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
