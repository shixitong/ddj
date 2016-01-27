package com.jdy.ddj.entity;

import com.jdy.ddj.common.entity.IdEntity;


/**
 *  */
public class Official extends IdEntity {
    /**
     * 状态（1=标题0=列表）
     */
    private String type;

    /**
     * 图片
     */
    private String phonos;

    /**
     * iso的当前版本
     */
    private String versionIos;

    /**
     * Android的当前版本
     */
    private String version;

    /**
     * 内容
     */
    private byte[] content;

    /**
     * 状态（1=标题0=列表）
     */
    public String getType() {
        return this.type;
    }

    /**
     * 状态（1=标题0=列表）
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 图片
     */
    public String getPhonos() {
        return this.phonos;
    }

    /**
     * 图片
     */
    public void setPhonos(String phonos) {
        this.phonos = phonos;
    }

    /**
     * iso的当前版本
     */
    public String getVersionIos() {
        return this.versionIos;
    }

    /**
     * iso的当前版本
     */
    public void setVersionIos(String versionIos) {
        this.versionIos = versionIos;
    }

    /**
     * Android的当前版本
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Android的当前版本
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 内容
     */
    public byte[] getContent() {
        return this.content;
    }

    /**
     * 内容
     */
    public void setContent(byte[] content) {
        this.content = content;
    }
}