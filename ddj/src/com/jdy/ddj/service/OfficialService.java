package com.jdy.ddj.service;

import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.entity.Official;
import java.util.*;

public interface OfficialService {
    /**
     * 保存对象
     */
    void save(Official official);

    /**
     * 根据ID删除指定记录
     */
    void deleteById(String id);

    /**
     * @param official 
	 * 根据ID,更新所有字段内容
     */
    void updateById(Official official);

    /**
     * @param pageRequest 
	 * 查询总数
     */
    long count(PageRequest pageRequest);

    /**
     * @param id 
	 * 按ID查询
     */
    Official getById(String id);

    /**
     * @param pageRequest 
	 * 分页查询
     */
    Page<Official> findPage(PageRequest pageRequest);

    /**
     * @param official 
	 * 根据ID,更新对象中不为空的字段内容
     */
    void updateValuesById(Official official);

    /**
     * @param official 
	 * 根据不为空的字段查询
     */
    List<Official> getByValues(Official official);

    /**
     * @param official 
	 * 保存或者更新信息
     */
    void saveOrUpdateById(Official official);

    /**
     * @param officialList 
	 *批量保存或者更新信息
     */
    void saveList(List<Official> officialList);

    /**
     * @param ids 
	 * 根据ID,批量删除多个
     */
    void deleteByIds(List<String> ids);
}