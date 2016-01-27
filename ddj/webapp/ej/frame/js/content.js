function getNavMenus(){
    if(NAV_ID!=""){
        $.get(
            /*CONTEXT_PATH + "/system/resource/getNavMenu.do",*/
            /*CONTEXT_PATH + "/system/getUserNavMenu.do",*/
            NAV_URL,
            {pid: NAV_ID,all: "YES",type: "2"},
            function(data){
                if(typeof(data)=="string") data = eval("("+data+")");
                createMenu(data);
            }
        );
    }
}
function createMenu(data){
    $("#menu_tree").tree({
        animate: true,
        data: data,
        lines: true,
        onClick: function(node){
            if($(this).tree('isLeaf', node.target)){
                var surl = node.attributes.src;
                surl = /^https?:/.test(surl) ? "" : CONTEXT_PATH + (/^\//.test(surl) ? "" : "/") + surl;
                switch (node.attributes.target){
                    case "_blank":
                        window.open(surl);
                        break;
                    case "_self":
                        self.location.href = surl;
                        break;
                    case "_parent":
                        self.parent.location.href = surl;
                        break;
                    default :
                        $("#content").panel("setTitle", node.text);
                        $("#contentframe").attr("src", surl);
                }
            }
        }
    });
}

/*
var templateData = [
    {
        id: "1",
        text: "单据填写",
        iconCls: "icon-app-new",
        state: "open",
        attributes: {
            hover: "",
            src: "/webframe/form/template.do",
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
                    src: "/csm/company/index.do",
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
                    src: "/edm/itemData/itemDataPage.do",
                    target: "panel"
                }
            },
            {
                id: "4",
                text: "宏观环境数据统计",
                iconCls: "icon-app-done",
                state: "open",
                attributes: {
                    hover: "",
                    src: "/edm/itemDataQuery/searchPage.do?dtype=1",
                    target: "_blank"
                }
            }
        ]
    }
];*/
