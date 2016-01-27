/**
 * Created with IntelliJ IDEA.
 * User: JinShaowei
 * Date: 12-11-5
 * Time: 下午3:55
 * Description:
 * 获取实心节点的扩展，可以单独获取实心节点
 */

$.extend($.fn.tree.methods, {
    //获取所有选中的节点（带实心的）
    getCheckedExt:function (jq) {
        var checked = $(jq).tree("getChecked");
        var checkbox2 = $(jq).find("span.tree-checkbox2").parent();
        $.each(checkbox2, function () {
            var node = $.extend({}, $.data(this, "tree-node"), {
                target:this
            });
            checked.push(node);
        });
        return checked;
    },
    //只获取实心节点
    getSolidExt:function (jq) {
        var checked = [];
        var checkbox2 = $(jq).find("span.tree-checkbox2").parent();
        $.each(checkbox2, function () {
            var node = $.extend({}, $.data(this, "tree-node"), {
                target:this
            });
            checked.push(node);
        });
        return checked;
    }
});
