package com.jdy.ddj.common.tools.importTools;

import java.util.List;

/**
 * User: zhangjunhui
 * DateTime: 12-12-19 下午7:06
 * Desc: Excel Vo，导入功能使用，模板 title = cells(1,1) , code = cells(1,2) , data = rows
 */
public class ExcelDataVo {
    private String code;
    private String title;
    private List<Object> data;

    public String getCode() {
        return code;
    }

    /**
     * Excel 模板代码，标识该模板
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Object> getData() {
        return data;
    }

    /**
     * Excel 中信息行List
     * @param data
     */
    public void setData(List<Object> data) {
        this.data = data;
    }
}
