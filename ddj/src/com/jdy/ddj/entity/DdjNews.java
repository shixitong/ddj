package com.jdy.ddj.entity;


import com.jdy.ddj.common.entity.IdEntity;

/**
 *  */
public class DdjNews extends IdEntity {
    /**
     * 类型（1滚动广告2新闻列表）
     */
    private String type;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String remark;

    /**
     * 图片
     */
    private String photo;

    /**
     * 详情地址
     */
    private String url;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 详细信息
     */
    private byte[] detail;

    /**
     * 类型（1滚动广告2新闻列表）
     */
    public String getType() {
        return this.type;
    }

    /**
     * 类型（1滚动广告2新闻列表）
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 标题
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 描述
     */
    public String getRemark() {
        return this.remark;
    }

    /**
     * 描述
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 图片
     */
    public String getPhoto() {
        return this.photo;
    }

    /**
     * 图片
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * 详情地址
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * 详情地址
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 创建时间
     */
    public String getCreateTime() {
        return this.createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * 详细信息
     */
    public byte[] getDetail() {
        return this.detail;
    }

    /**
     * 详细信息
     */
    public void setDetail(byte[] detail) {
        this.detail = detail;
    }


    private String strDetail;//详情字符串

    public String getStrDetail() {
        return strDetail;
    }

    public void setStrDetail(String strDetail) {
        this.strDetail = strDetail;
    }



}