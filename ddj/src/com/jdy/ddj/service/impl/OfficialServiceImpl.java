package com.jdy.ddj.service.impl;

import com.jdy.ddj.common.dao.RequestBean;
import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.dao.OfficialDao;
import com.jdy.ddj.entity.Official;
import com.jdy.ddj.service.OfficialService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("officialService")
@Transactional(rollbackFor = {Exception.class,RuntimeException.class})
public class OfficialServiceImpl implements OfficialService {
    private static final Logger logger = LoggerFactory.getLogger(OfficialServiceImpl.class);

    @Autowired()
    private OfficialDao officialDao;

    /**
     *  保存对象
     */
    public void save(Official official) {

	    officialDao.save(official);
    }

    /**
     *  根据ID删除指定记录
     */
    public void deleteById(String id) {
        officialDao.deleteById(id);
    }

    /**
     * @param official 
	 * 根据ID,更新所有字段内容
     */
    public void updateById(Official official) {

	    officialDao.updateById(official);
    }

    /**
     * @param pageRequest 
	 * 查询总数
     */
    public long count(PageRequest pageRequest) {
        RequestBean requestBean = RequestBean.request(pageRequest);
	    requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
	    return officialDao.count(requestBean);
    }

    /**
     * @param id 
	 * 按ID查询
     */
    public Official getById(String id) {
        return officialDao.getById(id);
    }

    /**
     * @param pageRequest 
	 * 分页查询
     */
    public Page<Official> findPage(PageRequest pageRequest) {
        RequestBean requestBean = RequestBean.request(pageRequest);
	    requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
	    return requestBean.getPage(officialDao.findPage(requestBean));
    }

    /**
     * @param official 
	 * 根据ID,更新对象中不为空的字段内容
     */
    public void updateValuesById(Official official) {

	    officialDao.updateValuesById(official);
    }

    /**
     * @param official 
	 * 根据不为空的字段查询
     */
    public List<Official> getByValues(Official official) {
        return officialDao.getByValues(official);
    }

    /**
     * @param official 
	 * 保存或者更新信息
     */
    public void saveOrUpdateById(Official official) {
        if(StringUtils.isNotEmpty(official.getId())&&(this.getById(official.getId())!=null)){
	        this.updateValuesById(official);
	    }else{
	        this.save(official);
	    }
    }

    /**
     * @paramofficialList 根据id批量保存或更新
     */
    public void saveList(List<Official> officialList) {
        if(officialList!=null&&officialList.size()>0){
	        for(Official official:officialList){
	            this.saveOrUpdateById(official);
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