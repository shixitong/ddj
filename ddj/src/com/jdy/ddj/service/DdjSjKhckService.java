package com.jdy.ddj.service;

import java.util.List;

import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.entity.DdjSjKhck;

/**
 * 
 * 司机关联客户仓库
 * @author Administrator
 *
 */
public interface DdjSjKhckService {

	/**
     * 保存对象
     */
    void save(DdjSjKhck ddjsjkhck);

    /**
     * 根据ID删除指定记录
     */
    void deleteById(DdjSjKhck key);

    /**
     * @param ddjsjkhck 
	 * 根据ID,更新所有字段内容
     */
    void updateById(DdjSjKhck ddjsjkhck);

    /**
     * @param pageRequest 
	 * 查询总数
     */
    long count(PageRequest pageRequest);

    /**
     * @param id 
	 * 按ID查询
     */
    DdjSjKhck getById(String id);

    /**
     * @param pageRequest 
	 * 分页查询
     */
    Page<DdjSjKhck> findPage(PageRequest pageRequest);

    /**
     * @param ddjsjkhck 
	 * 根据ID,更新对象中不为空的字段内容
     */
    void updateValuesById(DdjSjKhck ddjsjkhck);

    /**
     * @param ddjsjkhck 
	 * 根据不为空的字段查询
     */
    List<DdjSjKhck> getByValues(DdjSjKhck ddjsjkhck);

    /**
     * @param ddjsjkhck 
	 * 保存或者更新信息
     */
    void saveOrUpdateById(DdjSjKhck ddjsjkhck);

    /**
     * @param ddjsjkhckList 
	 *批量保存或者更新信息
     */
    void saveList(List<DdjSjKhck> ddjsjkhckList);

    /**
     * @param ids 
	 * 根据ID,批量删除多个
     */
    void deleteByIds(List<String> ids);
    
    
}
