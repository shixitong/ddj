package com.jdy.ddj.entity;


import com.jdy.ddj.common.entity.IdEntity;

import java.util.Date;

/**
 *  */
public class DdjAdmin extends IdEntity {


    /**
     * 管理员登陆名
     */
    private String aname;

    /**
     * 密码
     */
    private String apassword;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 类型（1前台、2水吧、3财务）
     */
    private String atype;

    /**
     * 其他信息
     */
    private String other;

    /**
     * 创建时间
     */
    private Date createdate;



    /**
     * 管理员登陆名
     */
    public String getAname() {
        return this.aname;
    }

    /**
     * 管理员登陆名
     */
    public void setAname(String aname) {
        this.aname = aname;
    }

    /**
     * 密码
     */
    public String getApassword() {
        return this.apassword;
    }

    /**
     * 密码
     */
    public void setApassword(String apassword) {
        this.apassword = apassword;
    }

    /**
     * 真实姓名
     */
    public String getName() {
        return this.name;
    }

    /**
     * 真实姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 类型（1前台、2水吧、3财务）
     */
    public String getAtype() {
        return this.atype;
    }

    /**
     * 类型（1前台、2水吧、3财务）
     */
    public void setAtype(String atype) {
        this.atype = atype;
    }

    /**
     * 其他信息
     */
    public String getOther() {
        return this.other;
    }

    /**
     * 其他信息
     */
    public void setOther(String other) {
        this.other = other;
    }

    /**
     * 创建时间
     */
    public Date getCreatedate() {
        return this.createdate;
    }

    /**
     * 创建时间
     */
    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }
}