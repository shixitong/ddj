package com.jdy.ddj.service;

import java.util.List;

import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.entity.DdjUser;

public interface UserService {

	 /**
     * 保存对象
     */
    void save(DdjUser ddjUser);

    /**
     * 根据ID删除指定记录
     */
    void deleteById(String id);

    /**
     * @param ddjAdmin 
	 * 根据ID,更新所有字段内容
     */
    void updateById(DdjUser ddjUser);

    /**
     * @param pageRequest 
	 * 查询总数
     */
    long count(PageRequest pageRequest);

    /**
     * @param id 
	 * 按ID查询
     */
    DdjUser getById(String id);

    /**
     * @param pageRequest 
	 * 分页查询
     */
    Page<DdjUser> findPage(PageRequest pageRequest);

    /**
     * @param ddjUser 
	 * 根据ID,更新对象中不为空的字段内容
     */
    void updateValuesById(DdjUser ddjUser);

    /**
     * @param ddjUser 
	 * 根据不为空的字段查询
     */
    List<DdjUser> getByValues(DdjUser ddjUser);

    /**
     * @param ddjUser 
	 * 保存或者更新信息
     */
    void saveOrUpdateById(DdjUser ddjUser);

    /**
     * @param ddjUserList 
	 *批量保存或者更新信息
     */
    void saveList(List<DdjUser> ddjUserList);

    /**
     * @param ids 
	 * 根据ID,批量删除多个
     */
    void deleteByIds(List<String> ids);
    
    List<DdjUser> getByIds(String ids);
    
    
}
