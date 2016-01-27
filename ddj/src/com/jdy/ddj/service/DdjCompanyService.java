package com.jdy.ddj.service;

import java.util.List;

import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.entity.DdjCompany;

public interface DdjCompanyService {

	 /**
     * 保存对象
     */
    void save(DdjCompany ddjCompany);

    /**
     * 根据ID删除指定记录
     */
    void deleteById(String id);

    /**
     * @param ddjCompany 
	 * 根据ID,更新所有字段内容
     */
    void updateById(DdjCompany ddjCompany);

    /**
     * @param pageRequest 
	 * 查询总数
     */
    long count(PageRequest pageRequest);

    /**
     * @param id 
	 * 按ID查询
     */
    DdjCompany getById(String id);

    /**
     * @param pageRequest 
	 * 分页查询
     */
    Page<DdjCompany> findPage(PageRequest pageRequest);

    /**
     * @param ddjCompany 
	 * 根据ID,更新对象中不为空的字段内容
     */
    void updateValuesById(DdjCompany ddjCompany);

    /**
     * @param ddjCompany 
	 * 根据不为空的字段查询
     */
    List<DdjCompany> getByValues(DdjCompany ddjCompany);

    /**
     * @param ddjCompany 
	 * 保存或者更新信息
     */
    void saveOrUpdateById(DdjCompany ddjCompany);

    /**
     * @param ddjCompanyList 
	 *批量保存或者更新信息
     */
    void saveList(List<DdjCompany> ddjCompanyList);

    /**
     * @param ids 
	 * 根据ID,批量删除多个
     */
    void deleteByIds(List<String> ids);
    
    List<DdjCompany> getJgdCjList(long useridKh);
    
    List<DdjCompany> getJgdKhList(long useridCj);
}
