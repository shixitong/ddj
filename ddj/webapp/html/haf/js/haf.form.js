/**
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
        var success = success || function(data){};
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
            width: "700px",
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
                        str = '<div style="width:300px;position:absolute;right:20px;top:12px;display:none"' +
                            ' id="att_search' + _file_uploader + '">' +
                            '<select style="width:200px" class="att-classify">' +
                            '<option value="">** 类型筛选 **</option>' +
                            '<option value="[NULL]">** [未分类] **</option>' +
                            '</select>' +
                            '<select style="width:80px" class="att-status">' +
                            '<option value="-1">** 状态筛选 **</option>' +
                            '<option value="0">未使用</option>' +
                            '<option value="1">使用中</option>' +
                            '</select>';
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
                            singleSelect: singleSelect,
                            url: list_url,
                            //queryParams: {usedStatus: 0},
                            idField: 'id',
                            columns: [[
                                {field:'id',checkbox:true},
                                {field:'originalName',title:'文件名称',width:200,align:'center'},
                                {field:'createDate',title:'上传时间',width:120,align:'center'},
                                {field:'extType',title:'类型',width:60,align:'center'},
                                {field:'fileSize',title:'大小',width:60,align:'center'},
                                {field:'classify',title:'分类',width:80,align:'center'},
                                {field:'usedCount',title:'使用',width:50,align:'center'},
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

                                $("#att_search" + _file_uploader + " select").unbind().change(function(){
                                    $("#att_list" + _file_uploader).datagrid("load", {
                                        classify: $(".att-classify",this).val(),
                                        usedStatus: $(".att-status",this).val()
                                    });
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
            $g.append('<span class="input-group-addon select-group-addon">选择..</span>');
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

    /** dataGrid Row Edit Dialog */
    /*options={
        width:"",
        title:"",
        hidden:["name1","name2"],
        visible:[
            [
                {label:"",name:"",readonly:false,type:"text",options:[["value","text"],["",""]],labelWidth:"2",fieldWidth:"4"},
                {}
            ],
            [{},{}]
        ],
        rowData:{},
        submit:function(formData){}
    }*/
    function _gridEditDialog(options){
        var _data = options.rowData || {};
        //
        var $form = $('<form class="form-horizontal"></form>');
        if(options.hidden && options.hidden.length){
            $.each(options.hidden, function(i,name){
                $form.append('<input type="hidden" name="' + name + '" value="' + (_data[name]||'') + '">');
            });
        }
        //
        $.each(options.visible, function(i,group){
            var $group = $('<div class="form-group"></div>');
            $.each(group, function(ii,field){
                $group.append('<label class="col-xs-' + (field.labelWidth||'2') + ' control-label">' + field.label + '</label>');
                var $field;
                if(options.type=="textarea"){
                    $field = $('<textarea name="' + field.name + '" class="form-control"></textarea>')
                }else if(options.type=="select"){
                    $field = $('<select name="' + field.name + '" class="form-control"><option value=""></option></select>');
                    $.each(field.options,function(iii,vv){
                        $field.append('<option value="' + vv[0] + '">' + (vv[1]||vv[0]) + '</option>');
                    });
                }else if(options.type=="radio" || options.type=="checkbox"){
                    var str = '';
                    $.each(field.options,function(iii,vv){
                        str += '<input value="' + vv[0] + '" name="' + field.name + '" type="' + field.type + '">' +
                            '' + (vv[1]||vv[0]) + '&nbsp;';
                    });
                    $field = $(str);
                }else{
                    $field = $('<input value="" name="' + field.name + '" type="text" class="form-control">');
                }
                if(field.readonly) $field.attr("readonly","readonly");
                //
                var $div = $('<div class="col-xs-' + (field.fieldWidth||'4') + '"></div>');
                $field.appendTo($div);
                $div.appendTo($group);
                $group.appendTo($form);
                //
                if(field.type=="date"){
                    _setDatepicker($field);
                }
                //
                if(_data[field.name]){
                    $field.val(_data[field.name]);
                }
            });
        });
        //
        var _ready = typeof options.onload=="function" ? options.onload : false;
        var _success = typeof options.callback=="function" ? options.callback : false;
        //
        HAF.dialog({
            key: "grid_edit",
            title: options.title || "新增/编辑",
            content: $form,
            width: options.width || "650px",
            ready: function($d){
                if(_ready) _ready($d);
            },
            success: function($d){
                if(_success){
                    var r = _success(_formToJson($d));
                    if(typeof r=="boolean" && r==false){
                        return false;
                    }
                }
                return true;
            }
        });
    }

    function _inArray(array,value){

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
        },
        gridEditor: function(options){
            _gridEditDialog(options);
        }
    });

})(window, undefined, jQuery);