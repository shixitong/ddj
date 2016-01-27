package com.jdy.ddj.service.impl;


import com.jdy.ddj.common.dao.RequestBean;
import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.common.utils.DateUtils;
import com.jdy.ddj.dao.DdjNewsDao;
import com.jdy.ddj.entity.DdjNews;
import com.jdy.ddj.service.DdjNewsService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("ddjNewsService")
@Transactional(rollbackFor = {Exception.class,RuntimeException.class})
public class DdjNewsServiceImpl implements DdjNewsService {
    private static final Logger logger = LoggerFactory.getLogger(DdjNewsServiceImpl.class);

    @Autowired()
    private DdjNewsDao ddjNewsDao;

    /**
     *  保存对象
     */
    public void save(DdjNews ddjNews) {
        if(StringUtils.isEmpty(ddjNews.getCreateTime())){
	        ddjNews.setCreateTime(DateUtils.getLongDateStr());
	    }

	    ddjNewsDao.save(ddjNews);
    }

    /**
     *  根据ID删除指定记录
     */
    public void deleteById(String id) {
        ddjNewsDao.deleteById(id);
    }

    /**
     * @param ddjNews 
	 * 根据ID,更新所有字段内容
     */
    public void updateById(DdjNews ddjNews) {

	    ddjNewsDao.updateById(ddjNews);
    }

    /**
     * @param pageRequest 
	 * 查询总数
     */
    public long count(PageRequest pageRequest) {
        RequestBean requestBean = RequestBean.request(pageRequest);
	    requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
	    return ddjNewsDao.count(requestBean);
    }

    /**
     * @param id 
	 * 按ID查询
     */
    public DdjNews getById(String id) {
        return ddjNewsDao.getById(id);
    }

    /**
     * @param pageRequest 
	 * 分页查询
     */
    public Page<DdjNews> findPage(PageRequest pageRequest) {
        RequestBean requestBean = RequestBean.request(pageRequest);
	    requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
	    return requestBean.getPage(ddjNewsDao.findPage(requestBean));
    }

    /**
     * @param ddjNews 
	 * 根据ID,更新对象中不为空的字段内容
     */
    public void updateValuesById(DdjNews ddjNews) {
	    ddjNewsDao.updateValuesById(ddjNews);
    }

    /**
     * @param ddjNews 
	 * 根据不为空的字段查询
     */
    public List<DdjNews> getByValues(DdjNews ddjNews) {
        return ddjNewsDao.getByValues(ddjNews);
    }

    /**
     * @param ddjNews 
	 * 保存或者更新信息
     */
    public void saveOrUpdateById(DdjNews ddjNews) {
        if(StringUtils.isNotEmpty(ddjNews.getId())&&(this.getById(ddjNews.getId())!=null)){
	        this.updateValuesById(ddjNews);
	    }else{
	        this.save(ddjNews);
	    }
    }

    /**
     * @paramddjNewsList 根据id批量保存或更新
     */
    public void saveList(List<DdjNews> ddjNewsList) {
        if(ddjNewsList!=null&&ddjNewsList.size()>0){
	        for(DdjNews ddjNews:ddjNewsList){
	            this.saveOrUpdateById(ddjNews);
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