package com.jdy.ddj.dao;

import com.jdy.ddj.common.dao.RequestBean;
import com.jdy.ddj.entity.DdjAdmin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DdjAdminDao {
    /**
     * 保存对象
     */
    void save(DdjAdmin ddjAdmin);

    /**
     * 根据ID删除指定记录
     */
    void deleteById(String id);

    /**
     * @param requestBean 
	 *根据条件进行批量删除.如果不传入适当条件,将删除整表内容
     */
    void deleteByWhere(@Param("requestBean") RequestBean requestBean);

    /**
     * @param ddjAdmin 
	 * 根据ID,更新所有字段内容
     */
    void updateById(DdjAdmin ddjAdmin);

    /**
     * @param requestBean 
	 * @param ddjAdmin 
	 *
     */
    void updateByWhere(@Param("requestBean") RequestBean requestBean, @Param("record") DdjAdmin ddjAdmin);

    /**
     * @param requestBean 
	 * 查询总数
     */
    long count(@Param("requestBean") RequestBean requestBean);

    /**
     * @param id 
	 * 按ID查询
     */
    DdjAdmin getById(String id);

    /**
     * @param requestBean 
	 * 根据条件查询
     */
    List<DdjAdmin> getByWhere(@Param("requestBean") RequestBean requestBean);

    /**
     * @param requestBean 
	 * 分页查询
     */
    List<DdjAdmin> findPage(@Param("requestBean") RequestBean requestBean);

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
}