package com.jdy.ddj.service;

import java.util.List;

import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.entity.DdjBjd;

/**
 * 报价单
 * @author NONE
 *
 */
public interface DdjBjdService {

	 /**
     * 保存对象
     */
    void save(DdjBjd ddjbjd);

    /**
     * 根据ID删除指定记录
     */
    void deleteById(String id);

    /**
     * @param ddjNews 
	 * 根据ID,更新所有字段内容
     */
    void updateById(DdjBjd ddjbjd);

    /**
     * @param pageRequest 
	 * 查询总数
     */
    long count(PageRequest pageRequest);

    /**
     * @param id 
	 * 按ID查询
     */
    DdjBjd getById(String id);

    /**
     * @param pageRequest 
	 * 分页查询
     */
    Page<DdjBjd> findPage(PageRequest pageRequest);

    /**
     * @param ddjbjd 
	 * 根据ID,更新对象中不为空的字段内容
     */
    void updateValuesById(DdjBjd ddjbjd);

    /**
     * @param ddjbjd 
	 * 根据不为空的字段查询
     */
    List<DdjBjd> getByValues(DdjBjd ddjbjd);

    /**
     * @param ddjbjd 
	 * 保存或者更新信息
     */
    void saveOrUpdateById(DdjBjd ddjbjd);

    /**
     * @param ddjbjdList 
	 *批量保存或者更新信息
     */
    void saveList(List<DdjBjd> ddjbjdList);

    /**
     * @param ids 
	 * 根据ID,批量删除多个
     */
    void deleteByIds(List<String> ids);
}
