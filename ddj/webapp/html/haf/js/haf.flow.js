/**
 * Created by zhangjunhui on 13-12-18.
 * version: 1.0
 */
(function(window, undefined, $){

    if(!window.HAF) return;
    if(!window.HAF.Flow) window.HAF.Flow = {};

    var _HafFlow = {
        variables: {
            isNewData:true, isNewFlow:true, isTaskPool:false, isClaimedTask:false, isMyAuthorized:false
        },
        methods:{}
    };

    function _get(k){
        return HAF.getValue(_HafFlow.variables, k, "");
    }
    function _set(k, v){
        HAF.setValue(_HafFlow.variables, k, v);
    }

    //获取多个key值，返回json  - keys为数组或者逗号分割字符串
    function _getVars(keys){
        var json = {};
        if(typeof keys=="string") keys = keys.split(",");
        $.each(keys,function(i,k){
            HAF.setValue(json, k, _get(k));
        });
        return json;
    }

    //流程预置处理人信息（审批权限配置表加载）
    var _autoPreAssigner = false;   //提交时自动收集表单上预设的审批人，调用 parsePreAssigner()

    //flowNodes，nodeButtons 转成 id为key的JSON对象
    var _oFlowNodes = null, _oNodeButtons = null;

    //历史流程环节，流程批示意见, 环节日志， 操作日志，任务日志
    var _flowHiTasks = null, _flowHiActions = null, _flowHiNodes = null, _flowHiComments;
    var _flowHiNodes0 = []; //记录hiNode对应的原始node属性

    //流程运行中信息
    var _flowContext = {
        assigners:{}, processVar:{}, subProcessVar:{},processParams:{}
    };

    //业务数据收集
    var _bizData = {};

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
        "create_comm": "/oa/service/uc/create.do",
        "get_comm": "/oa/service/uc/my/get.do",
        "get_assigner": "/oa/service/flow/getAssigner.do"
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
        parseProcessParams: null,   //流程分支参数处理方法：return json
        parseSubProcessVar: null,   //子流程变量处理方法：return json
        parseBizData: null,          //业务数据处理方法：return json
        checkButtons: function(buttons){}
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
        quit: {key: "quit", text: "退出", clsName: "btn-info"},
        save: {key: "save", text: "保存", clsName: "btn-primary"},
        submit: {key: "submit", text: "提交", clsName: "btn-success", confirm: true},
        reject: {key: "reject", text: "驳回", clsName: "btn-warning", hasMenu: true, confirm: true},
        restart: {key: "restart", text: "强制驳回", clsName: "btn-danger", hasMenu: true, confirm: true},
        append: {key: "append", text: "加签", clsName: "btn-info", hasMenu: true},
        handover: {key: "handover", text: "转办", clsName: "btn-info"},
        revoke: {key: "revoke", text: "撤签", clsName: "btn-info"},
        notify: {key: "notify", text: "通知", clsName: "btn-info"},
        deliver: {key: "deliver", text: "传阅", clsName: "btn-info"},
        authorize: {key: "authorize", text: "授权", clsName: "btn-info"},
        reauthorize: {key: "reauthorize", text: "收回授权", clsName: "btn-info"},
        claim: {key: "claim", text: "认领", clsName: "btn-info"},
        backpool: {key: "backpool", text: "退回任务池", clsName: "btn-info"},
        hangup: {key: "hangup", text: "挂起", clsName: "btn-info"},
        classify: {key: "classify", text: "归类", clsName: "btn-info", hasMenu: true},
        communicate: {key: "communicate", text: "沟通", clsName: "btn-info"},
        jump: {key: "jump", text: "跳转", clsName: "btn-warning", hasMenu: true, confirm: true},
        replace: {key: "replace", text: "替换", clsName: "btn-warning"},
        stop: {key: "stop", text: "终止", clsName: "btn-warning"},
        manage: {key: "manage", text: "权限管理", clsName: "btn-warning"},
        extend1: {key: "extend1", text: "扩展1", clsName: "btn-success"},
        extend2: {key: "extend2", text: "扩展2", clsName: "btn-success"},
        extend3: {key: "extend3", text: "扩展3", clsName: "btn-success"},
        print: {key: "print", text: "打印", clsName: "btn-info"},
        view: {key: "view", text: "查看流程", clsName: "btn-info"}
    }
    //默认显示的按钮
    var _buttons_def = ["quit",/*"print",*/"view"];
    //当新建流程时显示的按钮
    var _buttons_new = [];  //["save","submit"];
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
        var personName = _get("currentPerson.name");
        var flowName = _get("flowBase.name");
        _messages = {
            "submit": personName + _getButtonText("submit") + "了" + flowName + "，请处理",
            "reject": personName + _getButtonText("reject") + "了您的" + flowName + "，请查阅",
            "append": personName + _getButtonText("append") + "给您了" + flowName + "，请处理",
            "handover": personName + "将" + flowName + "转交给您" + "，请处理",
            "notify": personName + _getButtonText("notify") + "您及时处理" + flowName + "，请查阅",
            "deliver": personName + "传给您的" + flowName + "，请查阅",
            "authorize": personName + "授权您处理" + flowName + "，请查阅",
            "jump": personName + "将" + flowName + "跳转给您，请处理",
            "replace": personName + "将" + flowName + "处理人替换为您，请处理"
        };
    }

    //-- private (common) functions --------------------------
    /**
     * 判断开始、结束环节
     */
    function _isStartNode(nodeId){
        var nodeId = nodeId || _get("nodeId");
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
        var nodeId = nodeId || _get("nodeId");
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
        var nodeId = nodeId || _get("nodeId");
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
        var nodeId = nodeId || _get("nodeId");
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
        _flowContext = $.extend({assigners:{}, processVar:{}, subProcessVar:{},processParams:{}},
            _getVars("flowDefId,flowKeyId,instId,nodeId,taskUserId,executionId,dataId,formId"));
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
        //TODO 此处仅在测试期间使用，后续修改，添加后台权限控制
        if(_get("currentTask.assignerId")){
            _flowContext.operater = _get("currentTask.assignerId");    //默认以任务处理人身份操作流程（一般只有本人能打开自己的任务）
        }
    }
    function _setContext(k, v){
        HAF.setValue(_flowContext, k, v);
    }
    function _setAssigner(nodeId, user){
        HAF.setValue(_flowContext.assigners, nodeId, user);
    }
    function _setProcessVar(k, v){
        HAF.setValue(_flowContext.processVar, k, v);
    }
    function _setSubProcessVar(k, v){
        HAF.setValue(_flowContext.subProcessVar, k, v);
    }
    function _setBizData(k, v){
        HAF.setValue(_bizData, k, v);
    }
    //flow datas
    function _getFlowMessage(){
        var operateType = arguments[0] || _getOperateType();
        return _messages[operateType];
    }
    //
    function _getValue(obj,key){
        var v = HAF.getValue(obj,key,"");
        if(v==null||v=="null") v = "";
        return v;
    }

    //disabled button seconds
    function _disableButtons(){
        $(".haf-form-header .form-buttons .btn").each(function(){
            this.disabled = true;
        });
        window.setTimeout(function(){
            $(".haf-form-header .form-buttons .btn").each(function(){
                this.disabled = false;
            });
        },1500);
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
        _set(_options.ids);
        //新建的业务数据
        if(typeof _options.isNewData=="boolean"){
            _set("isNewData", _options.isNewData);
        }
        //替换指定的submitUrl
        if(_options.submitUrl){
            _urls.submit = HAF.basePath() + _options.submitUrl;
        }
        //获取流程及权限信息
        HAF.ajax({
            url: _urls.init,
            data: _getVars("flowKeyId,instId,nodeId,taskUserId,executionId,dataId,appKeyId"),
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
                        $(".haf-form-header").append('<div class="btn-toolbar form-buttons"><div class="btn-group"></div></div>');
                        var $bar = $(".haf-form-header .form-buttons .btn-group");
                        $.each(_buttons,function(k,o){
                            if(_oNodeButtons[k]){
                                var $btn = $('<button class="btn">'+o.text+'</button>');
                                var $grp = $('<div class="btn-group flow-btn-'+k+'"></div>');
                                $grp.append($btn).appendTo($bar);
                                if(o.clsName) $btn.addClass(o.clsName);
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
                    alert("流程初始化失败，系统中无相关流程信息");
                }
            }
        });
    }

    //初始化流程数据
    function _initData(flowData){
        //初始化到全局变量
        _set(flowData);
        //默认值处理
        var flowNodes = _get("flowNodes");
        if(_get("flowBase.id") && flowNodes && flowNodes.length){
            //flow _ids
            _set("flowDefId", _get("flowBase.id"));
            if(!_getFirstNode()){
                _setFirstNode(flowNodes[0].id);
            }
            if(_get("isNewFlow")){
                _set("nodeId", _getFirstNode());
            }else{
                if(!_get("instId")) _set("instId", _get("flowInst.actInstId"));
            }
            //当前环节
            if(_get("currentNode.nodeId")){
                _set("nodeId", _get("currentNode.nodeId"));
            }else if(_get("flowInst.currentNodeId")){
                _set("nodeId", _get("flowInst.currentNodeId"));
            }
            //流程环节
            _oFlowNodes = {};
            $.each(_get("flowNodes"), function(i,o){
                _oFlowNodes[o.id] = o;
            });
            //环节按钮
            _oNodeButtons = {};
            //默认按钮
            $.each(_buttons_def,function(i,k){
                _oNodeButtons[k] = _buttons[k];
            });
            //当非readonly时
            if( (typeof _options.readonly=="boolean" && _options.readonly)
                || (typeof _options.readonly=="function" && _options.readonly()) ){
                _set("isAuthor", false);
            }else{
                    var nodeButtons = _get("nodeButtons");
                    if(nodeButtons && nodeButtons.length){
                        $.each(nodeButtons, function(i,o){ _oNodeButtons[o.key] = o; });
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
                        if(_get("isNewFlow") || _isFirstNode()){
                            $.each(_buttons_new, function(i,k){
                                _oNodeButtons[k] = _buttons[k];
                            });
                        }
                    }
                    //有授权，显示“收回授权"
                    if(_get("isMyAuthorized")){
                        _oNodeButtons["reauthorize"] = _buttons["reauthorize"];
                    }else{
                        _oNodeButtons["reauthorize"] = false;
                    }
                    //任务池，认领
                    if(_get("isTaskPool") && !_get("isClaimedTask")){
                        //_oNodeButtons["claim"] = _buttons["claim"];
                    }else{
                        _oNodeButtons["claim"] = false;
                    }
                    //任务池，退回
                    if(!_get("isTaskPool") || !_get("isClaimedTask")){
                        _oNodeButtons["backpool"] = false;
                    }
                    //任务与特殊权限控制
                    if(_get("currentTask.id")){
                        _set("taskUserId",_get("currentTask.id"));
                        _set("executionId",_get("currentTask.actExecutionId"));
                        //权限约束
                        var lastOptType = _get("currentTask.lastOperateType"); //上次处理类型
                        if(lastOptType=="authorize"){
                            _oNodeButtons["authorize"] = false; //授权任务，不允许再授权
                            _oNodeButtons["handover"] = false;  //授权任务，不允许再转办
                        }else if(lastOptType=="handover"){
                            _oNodeButtons["authorize"] = false; //转办任务，不允许再授权
                            _oNodeButtons["handover"] = false;  //转办任务，不允许再转办
                        }else if(lastOptType=="append"){
                            _oNodeButtons["append"] = false;    //加签任务，不允许再加签
                        }
                        if(_get("currentTask.classify")==_classify_hangup){
                            _oNodeButtons["hangup"] = false;
                        }
                        //其它权限约束
                        if(typeof _options.checkButtons=="function") _options.checkButtons(_oNodeButtons);
                }
            }

            //流程默认消息
            _initDefMessages();
            //预置处理人
            var preAssigners = _get("preAssigners") || {};
            if(_get("instPreAssigners")) $.extend(true,preAssigners,_get("instPreAssigners"));
            if(preAssigners){
                $("input[flow-assigner-node][flow-assigner-field]").each(function(){
                    var n = $(this).attr("flow-assigner-node").split(",")[0];
                    var f = $(this).attr("flow-assigner-field");
                    if(preAssigners[n] && preAssigners[n][f]){
                        $(this).val(preAssigners[n][f]);
                    }
                });
            }
            //
            _flowHiComments = _get("flowHiComments");
            /*_flowHiNodes = _get("flowHiNodes");
            if(_flowHiNodes && _flowHiNodes.length){
                for(var i=0;i<_flowHiNodes.length;i++){
                    if(_flowHiNodes[i].nodeId!=_get("nodeId")) _flowHiNodes0.push(_oFlowNodes[_flowHiNodes[i].nodeId]);
                }
            }*/
            var flowHiNodeIds = _get("flowHiNodeIds");
            if(flowHiNodeIds && flowHiNodeIds.length){
                var oFlowHiNodeIds = {};
                $.each(flowHiNodeIds,function(i,v){oFlowHiNodeIds[v]=true;});
                for(var i=0;i<flowNodes.length;i++){
                    if(flowNodes[i].id==_get("nodeId")) break;
                    if(oFlowHiNodeIds[flowNodes[i].id])_flowHiNodes0.push(flowNodes[i]);
                }
            }
            //初始化成功
            return true;
        }else{
            return false;
        }
    }

    //跳转到指定环节
    function _jumpTo(node){
        var nextNode = typeof node=="object" ? node : _oFlowNodes[node];
        if(nextNode){
            _disableButtons();  //disabled button
            _setContext({targetNodeId: nextNode.id});
            $.get(
                _urls.get_node_user,
                { flowKeyId: _get("flowKeyId"), targetNodeId: nextNode.id, instId: _get("instId") },
                function(persons){
                    nextNode.assigners = persons;
                    _flowConfirm({
                        nextNodes: [nextNode],
                        //displays: {flowCommentRow:true},
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
            //deptLoad: _get("currentPerson.departmentId"),
            success: function(persons){
                var personName = HAF.getListValue(persons,"name",",");
                var personUsername = HAF.getListValue(persons,"sysUsername",",");
                _setContext({appendAuthors: personUsername, appendType: appendType});
                _flowConfirm({
                    displays: {flowCommentRow:true,nextNodeRow: true},
                    defaults: {nextPersonName: personName, nextPersonUsername: personUsername, operateName: _appendTypes[appendType]}
                });
            }
        });
    }
    //
    function _classifyTo(className){
        HAF.ajax({
            url: _urls.classify,
            data: {taskUserId: _get("taskUserId"),classify: className},
            success: function(data){
                if(data && data.success){
                    alert("分类标记完成");
                }
            }
        });
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
                nodeList.push(_flowHiNodes0[_flowHiNodes0.length-1]);
            }else if(buttonValue=="[toAnyNode]"){
                nodeList = _flowHiNodes0;
            }else if(!buttonValue || buttonValue=="[toStart]"){
                nodeList.push(_getNodeValue(_getFirstNode()));
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
                //deptLoad: _get("currentPerson.departmentId"),
                success: function(persons){
                    var personName = HAF.getListValue(persons,"name",",");
                    var personUsername = HAF.getListValue(persons,"sysUsername",",");
                    _setContext({newAssigner: personUsername});
                    _flowConfirm({
                        displays: {flowCommentRow:true,nextNodeRow:true},
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
                //deptLoad: _get("currentPerson.departmentId"),
                success: function(persons){
                    var personName = HAF.getListValue(persons,"name",",");
                    var personUsername = HAF.getListValue(persons,"sysUsername",",");
                    _setContext({notifyReceivers:personUsername});
                    _flowConfirm({
                        displays: {flowCommentRow:true,nextNodeRow:true},
                        defaults: {nextPersonName: personName, nextPersonUsername: personUsername}
                    });
                }
            });
        },
        deliver: function($e){
            HAF.dialog("selectPsn",{
                //deptLoad: _get("currentPerson.departmentId"),
                success: function(persons){
                    var personName = HAF.getListValue(persons,"name",",");
                    var personUsername = HAF.getListValue(persons,"sysUsername",",");
                    _flowConfirm({
                        displays: {flowCommentRow:true,nextNodeRow:true},
                        defaults: {nextPersonName: personName, nextPersonUsername: personUsername}
                    });
                }
            });
        },
        authorize: function($e){
            HAF.dialog("selectPsn",{
                //deptLoad: _get("currentPerson.departmentId"),
                singleSelect: true,
                success: function(persons){
                    var personName = HAF.getListValue(persons,"name",",");
                    var personUsername = HAF.getListValue(persons,"sysUsername",",");
                    _setContext({authorAssigner:personUsername});
                    _flowConfirm({
                        displays: {flowCommentRow:true,nextNodeRow:true},
                        defaults: {nextPersonName:personName,nextPersonUsername:personUsername}
                    });
                }
            });
        },
        reauthorize: function($e){
            var text = _get("currentTask.assignerName") + "[" + _get("currentTask.nodeName") + "]";
            if(confirm("确定【" + _getButtonText() + "】【" + text + "】？")){
                _commit();
            }
        },
        claim: function($e){
            if(confirm("确定【" + _getButtonText() + "】？")){
                HAF.ajax({
                    url: _urls.single_claim,
                    data: {taskUserId: _get("taskUserId")},
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
                //deptLoad: _get("currentPerson.departmentId"),
                singleSelect: true,
                success: function(persons){
                    var personName = HAF.getListValue(persons,"name",",");
                    var personUsername = HAF.getListValue(persons,"sysUsername",",");
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
                                $('<img src="' + _urls.view + "?" +
                                    HAF.urlSerialize(_getVars("flowDefId,flowKeyId,instId,executionId")) + '" style="display:none">'
                                ).load(function(){
                                    $loading.remove();
                                    $pane.append('<div class="flow-description"><h3>'+_get("flowBase.name")+'</h3>' +
                                        '<span>流程说明：</span>' + _get("flowBase.description") + '</div>'
                                    );
                                    $pane.css("text-align","center").find("img").css("display","inline-block");
                                }).appendTo($pane);
                            }else if(hiType=="comment"){
                                $loading.remove();
                                if(_flowHiComments && _flowHiComments.length){
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
                                }else{
                                    $pane.html("<p>未查询到相关记录</p>");
                                }
                            }else if(!_get("isNewFlow")){
                                $.get(_urls.get_history, {instId: _get("instId"), hiType: hiType}, function(data){
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
        },
        restart:function($e){
            if(_isEndNode()){
                _resetContext("restart");
            }else{
                _resetContext("reject");
            }
            _jumpTo(_getNodeValue(_getFirstNode()));
        },
        extend1: function($e){
            _disableButtons();
            if(typeof _options.extAction1=="function") _options.extAction1();
        },
        extend2: function($e){
            _disableButtons();
            if(typeof _options.extAction2=="function") _options.extAction2();
        },
        extend3: function($e){
            _disableButtons();
            if(typeof _options.extAction3=="function") _options.extAction3();
        }
    }

    //调用 _commands {} 中的方法
    function _command($e, cmd){
        var $e, cmd;
        for(var i=0;i<arguments.length;i++){
            if(typeof arguments[i]=="string") cmd = arguments[i];
            else $e = arguments[i];
        }
        if(!$e) $e = $(".haf-form-header .form-buttons .btn-group .flow-btn-" + cmd);
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
        _set("nextNodes",nextNodes);
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
                        //优先取表单上预置的审批人
                        var _ss = "input[flow-assigner-node*='" + nextNode.id + "']";
                        nextPersonUsername = $(_ss + "[flow-assigner-field='assignerId']").val() || "";
                        nextPersonName = $(_ss + "[flow-assigner-field='assignerName']").val() || nextPersonUsername;
                        if(nextPersonUsername){
                            $pRow.find("input[haf-comp-id='nextPersonName']").val(nextPersonName);
                            $pRow.find("input[haf-comp-id='nextPersonUsername']").val(nextPersonUsername);
                        }else{
                            if(nextNode.assigners && nextNode.assigners.length){
                                nextPersonUsername = HAF.getListValue(nextNode.assigners,"sysUsername",",");
                                nextPersonName = HAF.getListValue(nextNode.assigners,"name",",");
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
                                    key: "xx",
                                    backdrop: false,
                                    //deptLoad: _get("currentPerson.departmentId"),
                                    success: function(persons){
                                        var personNames = HAF.getListValue(persons, "name", ",");
                                        var personUsername = HAF.getListValue(persons, "sysUsername", ",");
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
                $dialog.find("div[haf-comp-id='tempComments'] span").hide();
                $dialog.find("div[haf-comp-id='tempComments'] span."+_getOperateType()).show().click(function(){
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
                if(nextNodes.length){
                    _setContext("nextNodeId",HAF.getListValue(nextNodes,"id"));
                }
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
                $dialog.find("input[haf-comp-id='subject']").val("关于“" + _get("flowBase.name") + "”，请回复");
                $dialog.find("input[haf-comp-id='refId']").val(_get("instId"));
                $dialog.find("input[haf-comp-id='refKey']").val(_get("nodeId"));
                $dialog.find("button[haf-comp-id='replierSelector']").click(function(){
                    var $inputs = $(this).parent().prev();
                    HAF.dialog("selectPsn",{
                        key: "xx",
                        backdrop: false,
                        //deptLoad: _get("currentPerson.departmentId"),
                        singleSelect: true,
                        success: function(persons){
                            var personNames = HAF.getListValue(persons, "name", ",");
                            var personUsername = HAF.getListValue(persons, "sysUsername", ",");
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
            if(typeof d=="object") HAF.setValue(_flowContext.processVar, d);
        }
    }
    function _parseProcessParams(){
        if(typeof _options.parseProcessParams=="function"){
            var d = _options.parseProcessParams();
            if(typeof d=="object") HAF.setValue(_flowContext.processParams, d);
        }
    }
    function _parseSubProcessVar(){
        if(typeof _options.parseSubProcessVar=="function"){
            var d = _options.parseSubProcessVar();
            if(typeof d=="object") HAF.setValue(_flowContext.subProcessVar, d);
        }
    }
    function _parseBizData(){
        if(typeof _options.parseBizData=="function"){
            var d = _options.parseBizData();
            if(typeof d=="object"){
                HAF.setValue(_bizData, d);
            }
        }
    }
    function _parsePreAssigner(){
        var preAssigners = {};
        $("input[flow-assigner-node][flow-assigner-field]").each(function(){
            var n = $(this).attr("flow-assigner-node").split(",");
            var f = $(this).attr("flow-assigner-field");
            var v = $(this).val();
            $.each(n, function(i, n1){
                if(!preAssigners[n1]) preAssigners[n1] = {};
                preAssigners[n1][f] = v;
            });
        });
        return _flowContext.preAssigners = $.extend({},preAssigners);
    }
    function _parseAllData(){
        _parseProcessVar();
        _parseProcessParams();
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
        _parseProcessVar();
        _parseProcessParams();
        _parseSubProcessVar();
        //_parseAllData();
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
            var r = _options.beforeCommit(_get("nextNodes"));
            if(typeof r=="boolean" && r==false) return false;
        }
        //
        _parseBizData();
        if(_autoPreAssigner){
            _parsePreAssigner();
        }
        //
        var commitData = {flowContext: _flowContext};
        if(!HAF.isEmpty(_bizData)) commitData.bizData = _bizData;
        //
        HAF.ajax({
            url: _urls.submit,
            contentType : 'application/json',
            data: HAF.stringify(commitData),
            success: function(data){
                if(data && data.success){
                    $.get("/portal/home/userMessage/flowSubmit/push.do?content=HAF&userId=" + _get("currentUsername"));
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

    //装载审批人
    //arr : [{nodeId:"",actDefId:"",appKey:""},{}], 不传参数或者null时获取流程所有默认配置的处理人
    function _loadAssigners(arr){
        HAF.ajax({
            url: _urls.get_assigner,
            contentType : 'application/json',
            data: HAF.stringify({actDefId: _get("flowDefId"), criteria: arr}),
            backdrop: false,
            success: function(preAssigners){
                if(preAssigners){
                    $("input[flow-assigner-node][flow-assigner-field]").each(function(){
                        var n = $(this).attr("flow-assigner-node").split(",")[0];
                        var f = $(this).attr("flow-assigner-field");
                        if(preAssigners[n] && preAssigners[n][f]){
                            $(this).val(preAssigners[n][f]);
                        }
                    });
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
        isNewData:function(){
            return _get("isNewData");
        },
        isNewFlow:function(){
            return _get("isNewFlow");
        },
        isStartNode: function(nodeId){
            return _isStartNode(nodeId);
        },
        isFirstNode: function(nodeId){
            return _isFirstNode(nodeId);
        },
        isLastNode: function(nodeId){
            return _isLastNode(nodeId);
        },
        isEndNode: function(nodeId){
            return _isEndNode(nodeId);
        },
        //public getter, setter
        get: function(k){
            return _get(k);
        },
        set: function(k, v){
            _set(k, v);
            return HAF.Flow;
        },
        getOperateType: function(){
            return _getOperateType();
        },
        getButtonText: function(operateType){
            return _getButtonText(operateType);
        },
        setProcessVar: function(k,v){
            _setProcessVar(k,v);
        },
        setSubProcessVar: function(k,v){
            _setSubProcessVar(k,v);
        },
        setAssigner: function(n,a){
            _setAssigner(n,a);
        },
        setBizData: function(k,v){
            _setBizData(k,v);
        },
        parsePreAssigner: function(){
            _parsePreAssigner();
        },
        autoPreAssigner: function(b){
            _autoPreAssigner = b;
        },
        cando: function(cmd){
            if(_oNodeButtons[cmd] && $(".haf-form-header .btn-group .flow-btn-" + cmd).is(":visible")) return true;
            else return false;
        },
        loadAssigners: function(arrKeys){
            _loadAssigners(arrKeys);
        },
        hideButton: function(key){
            $(".haf-form-header .btn-group .flow-btn-" + key).hide();
        },
        hideOptButtons: function(key){
            $(".haf-form-header .btn-group .flow-btn-" + key).hide();
        }
    });

})(window, undefined, jQuery);