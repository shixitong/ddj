package com.jdy.ddj.service;

import java.util.List;

import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.entity.DdjZdb;

/**
 * 
 * 账单表
 * @author Administrator
 *
 */
public interface DdjZdbService {

	 /**
     * 保存对象
     */
    void save(DdjZdb ddjzdb);

    /**
     * 根据ID删除指定记录
     */
    void deleteById(String id);

    /**
     * @param ddjzdb 
	 * 根据ID,更新所有字段内容
     */
    void updateById(DdjZdb ddjzdb);

    /**
     * @param pageRequest 
	 * 查询总数
     */
    long count(PageRequest pageRequest);

    /**
     * @param id 
	 * 按ID查询
     */
    DdjZdb getById(String id);

    /**
     * @param pageRequest 
	 * 分页查询
     */
    Page<DdjZdb> findPage(PageRequest pageRequest);

    /**
     * @param ddjzdb 
	 * 根据ID,更新对象中不为空的字段内容
     */
    void updateValuesById(DdjZdb ddjzdb);

    /**
     * @param ddjzdb 
	 * 根据不为空的字段查询
     */
    List<DdjZdb> getByValues(DdjZdb ddjzdb);

    /**
     * @param ddjzdb 
	 * 保存或者更新信息
     */
    void saveOrUpdateById(DdjZdb ddjzdb);

    /**
     * @param ddjckbList 
	 *批量保存或者更新信息
     */
    void saveList(List<DdjZdb> ddjzdbList);

    /**
     * @param ids 
	 * 根据ID,批量删除多个
     */
    void deleteByIds(List<String> ids);
}
