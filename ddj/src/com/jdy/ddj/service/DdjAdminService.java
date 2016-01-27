package com.jdy.ddj.service;

import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.entity.DdjAdmin;

import java.util.List;

public interface DdjAdminService {
    /**
     * 保存对象
     */
    void save(DdjAdmin ddjAdmin);

    /**
     * 根据ID删除指定记录
     */
    void deleteById(String id);

    /**
     * @param ddjAdmin 
	 * 根据ID,更新所有字段内容
     */
    void updateById(DdjAdmin ddjAdmin);

    /**
     * @param pageRequest 
	 * 查询总数
     */
    long count(PageRequest pageRequest);

    /**
     * @param id 
	 * 按ID查询
     */
    DdjAdmin getById(String id);

    /**
     * @param pageRequest 
	 * 分页查询
     */
    Page<DdjAdmin> findPage(PageRequest pageRequest);

    /**
     * @param ddjAdmin 
	 * 根据ID,更新对象中不为空的字段内容
     */
    void updateValuesById(DdjAdmin ddjAdmin);

    /**
     * @param ddjAdmin 
	 * 根据不为空的字段查询
     */
    List<DdjAdmin> getByValues(DdjAdmin ddjAdmin);

    /**
     * @param ddjAdmin 
	 * 保存或者更新信息
     */
    void saveOrUpdateById(DdjAdmin ddjAdmin);

    /**
     * @param ddjAdminList 
	 *批量保存或者更新信息
     */
    void saveList(List<DdjAdmin> ddjAdminList);

    /**
     * @param ids 
	 * 根据ID,批量删除多个
     */
    void deleteByIds(List<String> ids);
}