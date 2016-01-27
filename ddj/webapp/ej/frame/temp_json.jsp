<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
[
    {
        id: "1",
        text: "单据填写(template)",
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
                id: "3",
                text: "数据查看",
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
                text: "数据统计",
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
]