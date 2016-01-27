package com.jdy.ddj.service;

import java.util.List;

import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.entity.DdjCkb;

/**
 * 
 * 仓库表
 * @author NONE
 *
 */
public interface DdjCkbService {

	 /**
     * 保存对象
     */
    void save(DdjCkb ddjckb);

    /**
     * 根据ID删除指定记录
     */
    void deleteById(String id);

    /**
     * @param ddjckb 
	 * 根据ID,更新所有字段内容
     */
    void updateById(DdjCkb ddjckb);

    /**
     * @param pageRequest 
	 * 查询总数
     */
    long count(PageRequest pageRequest);

    /**
     * @param id 
	 * 按ID查询
     */
    DdjCkb getById(String id);

    /**
     * @param pageRequest 
	 * 分页查询
     */
    Page<DdjCkb> findPage(PageRequest pageRequest);

    /**
     * @param ddjckb 
	 * 根据ID,更新对象中不为空的字段内容
     */
    void updateValuesById(DdjCkb ddjckb);

    /**
     * @param ddjckb 
	 * 根据不为空的字段查询
     */
    List<DdjCkb> getByValues(DdjCkb ddjckb);

    /**
     * @param ddjckb 
	 * 保存或者更新信息
     */
    void saveOrUpdateById(DdjCkb ddjckb);

    /**
     * @param ddjckbList 
	 *批量保存或者更新信息
     */
    void saveList(List<DdjCkb> ddjckbList);

    /**
     * @param ids 
	 * 根据ID,批量删除多个
     */
    void deleteByIds(List<String> ids);
}
