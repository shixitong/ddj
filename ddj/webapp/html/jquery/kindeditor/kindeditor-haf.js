/**
 * User: zhangjunhui
 * Date: 12-5-31
 * Time: 上午11:38
 * 定义haf系统默认的kindeditor属性
 */
var haf_koptions = {
    themeType : 'default',
    langType : 'zh_CN',
    newlineTag : 'p',
    resizeType : 1,
    syncType : 'form',
    pasteType : 2,
    filterMode: false,
    dialogAlignType : 'page',
    minWidth : 650,
    minHeight : 100,
    minChangeSize : 5,
    items : [
        'preview', 'print','|','undo', 'redo', '|',  'template','cut', 'copy', 'paste',
        'plainpaste', 'wordpaste', '|', 'justifyleft', 'justifycenter', 'justifyright',
        'justifyfull', 'insertorderedlist', 'insertunorderedlist', 'indent', 'outdent', 'subscript',
        'superscript', 'clearhtml', 'quickformat', 'selectall', '|', 'fullscreen', '/',
        'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold',
        'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat', '|',
        'table', 'hr', 'link', 'unlink'
    ],
    colorTable : [
        ['#E53333', '#E56600', '#FF9900', '#64451D', '#DFC5A4', '#FFE500'],
        ['#009900', '#006600', '#99BB00', '#B8D100', '#60D978', '#00D5FF'],
        ['#337FE5', '#003399', '#4C33E5', '#9933E5', '#CC33E5', '#EE33EE'],
        ['#FFFFFF', '#CCCCCC', '#999999', '#666666', '#333333', '#000000']
    ],
    fontSizeTable : ['9px', '10px', '12px', '14px', '16px', '18px', '24px', '32px']
};
var koptions_r_items = [
    'preview', 'print', '|', 'formatblock', 'fontname', 'fontsize', '|', 'forecolor', 'hilitecolor', 'bold',
    'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat'
];
// min
var haf_koptions_min = {
    themeType : 'default',
    langType : 'zh_CN',
    newlineTag : 'p',
    resizeType : 1,
    syncType : 'form',
    pasteType : 2,
    filterMode: false,
    dialogAlignType : 'page',
    minWidth : 650,
    minHeight : 100,
    minChangeSize : 5,
    items : [
        'fullscreen','preview','|','undo', 'redo',
        '|','justifyleft', 'justifycenter', 'justifyright','justifyfull',
        'formatblock', 'fontname', 'fontsize',
        'forecolor', 'hilitecolor', 'bold', 'italic', 'underline', 'strikethrough', 'lineheight', 'removeformat',
        '|','table', 'hr', 'link', 'unlink',
        '|','clearhtml', 'quickformat','plainpaste', 'wordpaste'
    ],
    colorTable : [
        ['#E53333', '#E56600', '#FF9900', '#64451D', '#DFC5A4', '#FFE500'],
        ['#009900', '#006600', '#99BB00', '#B8D100', '#60D978', '#00D5FF'],
        ['#337FE5', '#003399', '#4C33E5', '#9933E5', '#CC33E5', '#EE33EE'],
        ['#FFFFFF', '#CCCCCC', '#999999', '#666666', '#333333', '#000000']
    ],
    fontSizeTable : ['9px', '10px', '12px', '14px', '16px', '18px', '24px', '32px']
};
//readonly
var haf_koptions_readonly = {
    themeType : 'default',
    langType : 'zh_CN',
    newlineTag : 'p',
    resizeType : 1,
    syncType : 'form',
    pasteType : 2,
    filterMode: false,
    dialogAlignType : 'page',
    minWidth : 650,
    minHeight : 100,
    minChangeSize : 5,
    items : ['fullscreen','preview'],
    noDisableItems: ['fullscreen','preview','print'],
    colorTable : [
        ['#E53333', '#E56600', '#FF9900', '#64451D', '#DFC5A4', '#FFE500'],
        ['#009900', '#006600', '#99BB00', '#B8D100', '#60D978', '#00D5FF'],
        ['#337FE5', '#003399', '#4C33E5', '#9933E5', '#CC33E5', '#EE33EE'],
        ['#FFFFFF', '#CCCCCC', '#999999', '#666666', '#333333', '#000000']
    ],
    fontSizeTable : ['9px', '10px', '12px', '14px', '16px', '18px', '24px', '32px'],
    readonlyMode: true,
    noReadonlyToolbar: true
};
