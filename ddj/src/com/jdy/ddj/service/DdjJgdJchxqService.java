package com.jdy.ddj.service;

import java.util.List;

import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.entity.DdjJgdJchxq;

/**
 * 加工单进出货详情
 * @author Administrator
 *
 */
public interface DdjJgdJchxqService {

	/**
     * 保存对象
     */
    void save(DdjJgdJchxq ddjJgdJchxq);

    /**
     * 根据ID删除指定记录
     */
    void deleteById(String id);

    /**
     * @param ddjJgdJchxq 
	 * 根据ID,更新所有字段内容
     */
    void updateById(DdjJgdJchxq ddjJgdJchxq);

    /**
     * @param pageRequest 
	 * 查询总数
     */
    long count(PageRequest pageRequest);

    /**
     * @param id 
	 * 按ID查询
     */
    DdjJgdJchxq getById(String id);

    /**
     * @param pageRequest 
	 * 分页查询
     */
    Page<DdjJgdJchxq> findPage(PageRequest pageRequest);

    /**
     * @param ddjJgdJchxq 
	 * 根据ID,更新对象中不为空的字段内容
     */
    void updateValuesById(DdjJgdJchxq ddjJgdJchxq);

    /**
     * @param ddjJgdJchxq 
	 * 根据不为空的字段查询
     */
    List<DdjJgdJchxq> getByValues(DdjJgdJchxq ddjJgdJchxq);

    /**
     * @param ddjJgdJchxq 
	 * 保存或者更新信息
     */
    void saveOrUpdateById(DdjJgdJchxq ddjJgdJchxq);

    /**
     * @param ddjJgdJchxqList 
	 *批量保存或者更新信息
     */
    void saveList(List<DdjJgdJchxq> ddjJgdJchxqList);

    /**
     * @param ids 
	 * 根据ID,批量删除多个
     */
    void deleteByIds(List<String> ids);
}
