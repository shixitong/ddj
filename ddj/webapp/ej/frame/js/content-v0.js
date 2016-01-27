function createMenu(data){
    $("#menu_tree").tree({
        animate: true,
        data: data,
        onClick: function(node){
            if($(this).tree('isLeaf', node.target)){
                switch (node.attributes.target){
                    case "_blank":
                        window.open(node.attributes.src);
                        break;
                    case "_self":
                        self.location.href = node.attributes.src;
                        break;
                    case "_parent":
                        self.parent.location.href = node.attributes.src;
                        break;
                    case "frame":
                        var _loading = $("#panel_loading");
                        var _panel = $("#content_panel");
                        _loading.show();
                        _panel.hide().panel({
                            title: node.text,
                            content: "<iframe scrolling='auto' frameborder='0' width='100%' height='100%' src='"+node.attributes.src+"'></iframe>"
                        }).panel("body").find("> iframe").load(function(){
                            _loading.hide();
                            _panel.show();
                        });
                        break;
                    default :
                        $("#panel_loading").show();
                        $("#content_panel").hide().panel("refresh",node.attributes.src).panel("setTitle",node.text);
                    /*css("visibility","visible")*/
                }
            }
        }
    });
}

var templateData = [
    {
        id: "1",
        text: "单据填写",
        iconCls: "icon-app-new",
        state: "open",
        attributes: {
            hover: "",
            src: "/hxoa",
            target: "_blank"
        }
    },
    {
        id: "22",
        text: "信息查看",
        state: "open",
        attributes: {
            hover: "",
            src: "",
            target: ""
        },
        children: [
            {
                id: "2",
                text: "安全第三方",
                iconCls: "icon-app-draft",
                state: "open",
                attributes: {
                    hover: "",
                    src: CONTEXT_PATH+"/csm/company/index.do",
                    target: "frame"
                }
            },
            {
                id: "3",
                text: "宏观环境数据",
                iconCls: "icon-app-doing",
                state: "open",
                attributes: {
                    hover: "",
                    src: CONTEXT_PATH+"/edm/itemData/itemDataPage.do",
                    target: "panel"
                }
            },
            {
                id: "4",
                text: "OA测试系统",
                iconCls: "icon-app-done",
                state: "open",
                attributes: {
                    hover: "",
                    src: "http://hxoadeva.hxoadev.com",
                    target: "frame"
                }
            }
        ]
    }
];