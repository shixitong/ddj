package com.jdy.ddj.common.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * 统一定义id的entity基类.
 *
 * 基类统一定义id的属性名称、数据类型、列名映射及生成策略. 子类可重载getId()函数重定义id的列名映射和生成策略.
 *
 */
// JPA 基类的标识

public abstract class IdEntity implements Serializable {

    protected String id= UUID.randomUUID().toString().replaceAll("-", "");

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
/*    private  Integer  id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }*/
}
