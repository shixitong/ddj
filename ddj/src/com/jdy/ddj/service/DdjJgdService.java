package com.jdy.ddj.service;

import java.util.List;
import java.util.Map;

import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.entity.DdjJgd;

/**
 * 
 * 工单服务类
 * @author Administrator
 *
 */
public interface DdjJgdService {

	 /**
     * 保存对象
     */
    void save(DdjJgd ddjjgd);

    /**
     * 根据ID删除指定记录
     */
    void deleteById(String id);

    /**
     * @param ddjjgd 
	 * 根据ID,更新所有字段内容
     */
    void updateById(DdjJgd ddjjgd);

    /**
     * @param pageRequest 
	 * 查询总数
     */
    long count(PageRequest pageRequest);

    /**
     * @param id 
	 * 按ID查询
     */
    DdjJgd getById(String id);

    /**
     * @param pageRequest 
	 * 分页查询
     */
    Page<DdjJgd> findPage(PageRequest pageRequest);

    /**
     * @param ddjjgd 
	 * 根据ID,更新对象中不为空的字段内容
     */
    void updateValuesById(DdjJgd ddjjgd);

    /**
     * @param ddjjgd 
	 * 根据不为空的字段查询
     */
    List<DdjJgd> getByValues(DdjJgd ddjjgd);
    
    List<Map> getTotalStat(DdjJgd ddjjgd);

    /**
     * @param ddjjgd 
	 * 保存或者更新信息
     */
    void saveOrUpdateById(DdjJgd ddjjgd);

    /**
     * @param ddjbjdList 
	 *批量保存或者更新信息
     */
    void saveList(List<DdjJgd> ddjjgdList);

    /**
     * @param ids 
	 * 根据ID,批量删除多个
     */
    void deleteByIds(List<String> ids);
}
