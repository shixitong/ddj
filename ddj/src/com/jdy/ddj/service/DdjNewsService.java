package com.jdy.ddj.service;

import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.entity.DdjNews;

import java.util.List;

public interface DdjNewsService {
    /**
     * 保存对象
     */
    void save(DdjNews ddjNews);

    /**
     * 根据ID删除指定记录
     */
    void deleteById(String id);

    /**
     * @param ddjNews 
	 * 根据ID,更新所有字段内容
     */
    void updateById(DdjNews ddjNews);

    /**
     * @param pageRequest 
	 * 查询总数
     */
    long count(PageRequest pageRequest);

    /**
     * @param id 
	 * 按ID查询
     */
    DdjNews getById(String id);

    /**
     * @param pageRequest 
	 * 分页查询
     */
    Page<DdjNews> findPage(PageRequest pageRequest);

    /**
     * @param ddjNews 
	 * 根据ID,更新对象中不为空的字段内容
     */
    void updateValuesById(DdjNews ddjNews);

    /**
     * @param ddjNews 
	 * 根据不为空的字段查询
     */
    List<DdjNews> getByValues(DdjNews ddjNews);

    /**
     * @param ddjNews 
	 * 保存或者更新信息
     */
    void saveOrUpdateById(DdjNews ddjNews);

    /**
     * @param ddjNewsList 
	 *批量保存或者更新信息
     */
    void saveList(List<DdjNews> ddjNewsList);

    /**
     * @param ids 
	 * 根据ID,批量删除多个
     */
    void deleteByIds(List<String> ids);
}