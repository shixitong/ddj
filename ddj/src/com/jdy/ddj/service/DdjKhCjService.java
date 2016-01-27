package com.jdy.ddj.service;

import java.util.List;

import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.entity.DdjKhCj;

/**
 * 客户厂家关联
 * 1、客户和厂家合作
 * 2、客户取消和参加合作
 * @author none
 *
 */
public interface DdjKhCjService {
	 /**
     * 保存对象
     */
    void save(DdjKhCj ddjkhcj);

    /**
     * 删除指定记录
     */
    void deleteById(DdjKhCj ddjkhcj);

    /**
     * @param ddjkhcj 
	 * 根据ID,更新所有字段内容
     */
    void updateById(DdjKhCj ddjkhcj);

    /**
     * @param pageRequest 
	 * 查询总数
     */
    long count(PageRequest pageRequest);

    /**
     * @param id 
	 * 按ID查询
     */
    DdjKhCj  getById(String id);

    /**
     * @param pageRequest 
	 * 分页查询
     */
    Page<DdjKhCj> findPage(PageRequest pageRequest);

    /**
     * @param ddjkhcj 
	 * 根据ID,更新对象中不为空的字段内容
     */
    void updateValuesById(DdjKhCj ddjkhcj);

    /**
     * @param ddjkhcj 
	 * 根据不为空的字段查询
     */
    List<DdjKhCj> getByValues(DdjKhCj ddjkhcj);

    /**
     * @param ddjkhcj 
	 * 保存或者更新信息
     */
    void saveOrUpdateById(DdjKhCj ddjkhcj);

    /**
     * @param ddjUserList 
	 *批量保存或者更新信息
     */
    void saveList(List<DdjKhCj> ddjkhcjList);

    /**
     * @param ids 
	 * 根据ID,批量删除多个
     */
    void deleteByIds(List<String> ids);
    
    List<String> getCompnayIdByValues(DdjKhCj ddjkhcj);
}
