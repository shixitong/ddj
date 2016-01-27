package com.jdy.ddj.service.impl;


import com.jdy.ddj.common.dao.RequestBean;
import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.dao.DdjAdminDao;
import com.jdy.ddj.entity.DdjAdmin;
import com.jdy.ddj.service.DdjAdminService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("ddjAdminService")
@Transactional(rollbackFor = {Exception.class,RuntimeException.class})
public class DdjAdminServiceImpl implements DdjAdminService {
    private static final Logger logger = LoggerFactory.getLogger(DdjAdminServiceImpl.class);

    @Autowired()
    private DdjAdminDao ddjAdminDao;

    /**
     *  保存对象
     */
    public void save(DdjAdmin ddjAdmin) {

	    ddjAdminDao.save(ddjAdmin);
    }

    /**
     *  根据ID删除指定记录
     */
    public void deleteById(String id) {
        ddjAdminDao.deleteById(id);
    }

    /**
     * @param ddjAdmin 
	 * 根据ID,更新所有字段内容
     */
    public void updateById(DdjAdmin ddjAdmin) {

	    ddjAdminDao.updateById(ddjAdmin);
    }

    /**
     * @param pageRequest 
	 * 查询总数
     */
    public long count(PageRequest pageRequest) {
        RequestBean requestBean = RequestBean.request(pageRequest);
	    requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
	    return ddjAdminDao.count(requestBean);
    }

    /**
     * @param id 
	 * 按ID查询
     */
    public DdjAdmin getById(String id) {
        return ddjAdminDao.getById(id);
    }

    /**
     * @param pageRequest 
	 * 分页查询
     */
    public Page<DdjAdmin> findPage(PageRequest pageRequest) {
        RequestBean requestBean = RequestBean.request(pageRequest);
	    requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
	    return requestBean.getPage(ddjAdminDao.findPage(requestBean));
    }

    /**
     * @param ddjAdmin 
	 * 根据ID,更新对象中不为空的字段内容
     */
    public void updateValuesById(DdjAdmin ddjAdmin) {

	    ddjAdminDao.updateValuesById(ddjAdmin);
    }

    /**
     * @param ddjAdmin 
	 * 根据不为空的字段查询
     */
    public List<DdjAdmin> getByValues(DdjAdmin ddjAdmin) {
        return ddjAdminDao.getByValues(ddjAdmin);
    }

    /**
     * @param ddjAdmin 
	 * 保存或者更新信息
     */
    public void saveOrUpdateById(DdjAdmin ddjAdmin) {
        if(StringUtils.isNotEmpty(ddjAdmin.getId())&&(this.getById(ddjAdmin.getId())!=null)){
	        this.updateValuesById(ddjAdmin);
	    }else{
	        this.save(ddjAdmin);
	    }
    }

    /**
     * @paramddjAdminList 根据id批量保存或更新
     */
    public void saveList(List<DdjAdmin> ddjAdminList) {
        if(ddjAdminList!=null&&ddjAdminList.size()>0){
	        for(DdjAdmin ddjAdmin:ddjAdminList){
	            this.saveOrUpdateById(ddjAdmin);
	        }
	    }
    }

    /**
     * @param ids 
	 * 根据ID,批量删除多个
     */
    public void deleteByIds(List<String> ids) {
        if(ids!=null&&ids.size()>0){
	        for(String id:ids){
	            this.deleteById(id);
	        }
	    }
    }
}