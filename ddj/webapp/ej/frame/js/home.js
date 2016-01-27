function getNavMenus(){
    $.get(
        /*CONTEXT_PATH + "/system/resource/getNavMenu.do",*/
        CONTEXT_PATH + "/system/getUserNavMenu.do",
        {pid: "ROOT",all: "YES",type: "1"},
        function(data){
            if(typeof(data)=="string") data = eval("("+data+")");
            createNav(data);
        }
    );
}
function createNav(data){
    var _nav = $("#nav_body");
    if(!_nav.is("ul")){
        _nav.append("<ul class='nav'></ul>");
        _nav = _nav.find("ul");
    }
    $.each(data,function(){
        _nav.append(getNthItems(this));
    });
    _nav.find("> li").addClass("top").not(":last").addClass("rightline");
    _nav.find("li.top > ul").addClass("ftop");

    $("ul.nav li").hover(
        function(){
            var _n = $(this).find("> ul");
            if(!_n.is(".ftop")){
                var ri = $(this).offset().left + $(this).width();
                if(_n.width()+ri > _nav.offset().left+_nav.width()){
                    _n.removeClass("fleft fright").addClass("fleft");
                }else{
                    _n.removeClass("fleft fright").addClass("fright");
                }
            }
            _n.show();
        },function(){
            $(this).find("> ul").hide();
        }
    );

    function getNthItems(item){
        item.src = item.src ? ((/^https?:/.test(item.src) ? "" : CONTEXT_PATH + (/^\//.test(item.src) ? "" : "/")) + item.src + (item.passNavid=="1" ? (/\?/.test(item.src) ? "&" : "?")+"curNavId="+item.id : "")) : "javascript:void(0)"; //(CONTEXT_PATH+"/webframe/content.do?curNavId="+item.id);
        var _item = $("<li><a href='" + item.src + "' target='" + item.target + "'" + ( item.hover ? " title='"+item.hover+"'" : "" ) + "><div>" + item.title + "</div></a></li>");
        if(item.frameHeight){
            _item.find("> a").click(function(){
                $("iframe[name='"+item.target+"']").height(item.frameHeight);
            });
        }
        if(item.children && item.children.length){
            var _item0 = $("<ul class='subnav'></ul>");
            $.each(item.children,function(){
                _item0.append(getNthItems(this));
            });
            _item.append(_item0);
        }
        return _item;
    }
};

/*
var templateData = [
    {
        id: "1",
        title: "首页",
        hover: "",
        src: "/hxoa",
        target: "_self",
        children: null
    },
    {
        id: "2",
        title: "财务管理",
        hover: "财务借支与报销",
        src: "",
        target: "",
        children: [
            {
                id: "21",
                title: "借支管理",
                hover: "员工借支",
                src: "http://hxoadeva.hxoadev.com/Produce/DesignTools.nsf/ViewShow_form?OpenForm&login&thDir=Application/FICO&thDb=HXFICOCN00Loan.nsf&thView=LoanDoingView&count=24&page=1&checkboxFlag=yes&realWidthflag=yes&seeFlag=yes&bdoptdesname=待处理",
                target: "mainframe",
                frameHeight: "570",
                children: null
            },
            {
                id: "22",
                title: "报销管理",
                hover: "员工报销",
                src: "",
                target: "",
                children: [
                    {
                        id: "221",
                        title: "基础配置",
                        hover: "",
                        src: "",
                        target: "",
                        frameHeight: "",
                        children: [
                            {
                                id: "2211",
                                title: "test",
                                hover: "",
                                src: "/webframe/content.do?defaultPage=edm/itemData/itemDataPage.do&defaultTitle=%E6%AC%A2%E8%BF%8E%E4%BD%BF%E7%94%A8",
                                target: "mainframe",
                                frameHeight: "550px",
                                passNavid: "1",
                                children: null
                            },
                            {
                                id: "2212",
                                title: "环境数据",
                                hover: "",
                                src: "/edm/itemData/itemDataPage.do",
                                passNavid: "1",
                                target: "mainframe",
                                frameHeight: "600px",
                                children: null
                            }
                        ]
                    },
                    {
                        id: "222",
                        title: "手机费报销",
                        hover: "",
                        src: "webframe/content.do",
                        target: "mainframe",
                        frameHeight: "500px",
                        passNavid: "1",
                        children: null
                    },
                    {
                        id: "223",
                        title: "行车费报销",
                        hover: "",
                        src: "test2.html",
                        target: "mainframe",
                        frameHeight: "400px",
                        children: null
                    }
                ]
            }
        ]
    },
    {
        id: "1",
        title: "销售管理",
        hover: "",
        src: "http://oaa.huaxincem.com",
        target: "_self",
        children: null
    },
    {
        id: "1",
        title: "生产管理",
        hover: "",
        src: "http://oaa.huaxincem.com",
        target: "_self",
        children: null
    },
    {
        id: "1",
        title: "安全管理",
        hover: "",
        src: "http://oaa.huaxincem.com",
        target: "_self",
        children: null
    },
    {
        id: "1",
        title: "门户网站",
        hover: "",
        src: "http://oaa.huaxincem.com",
        target: "_self",
        children: [
            {
                id: "1",
                title: "事业部门户",
                hover: "",
                src: "http://oaa.huaxincem.com",
                target: "_self",
                children: null
            },
            {
                id: "1",
                title: "职能门户",
                hover: "",
                src: "http://oaa.huaxincem.com",
                target: "_self",
                children: [
                    {
                        id: "1",
                        title: "采购门户",
                        hover: "",
                        src: "http://oaa.huaxincem.com",
                        target: "_self",
                        children: null
                    },
                    {
                        id: "1",
                        title: "营销门户",
                        hover: "",
                        src: "http://oaa.huaxincem.com",
                        target: "_self",
                        children: null
                    }
                ]
            }
        ]
    }
];*/
