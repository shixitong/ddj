package com.jdy.ddj.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.common.utils.DateUtils;
import com.jdy.ddj.dao.DdjKhCjMapper;
import com.jdy.ddj.entity.DdjKhCj;
import com.jdy.ddj.service.DdjKhCjService;


/**
 * 客户和厂家关联
 * @author NONE
 *
 */
@Service("ddjkhcjService")
@Transactional(rollbackFor = {Exception.class,RuntimeException.class})
public class DdjKhCjServiceImpl implements DdjKhCjService {

	
	//日志
	private static final Logger logger = LoggerFactory.getLogger(DdjKhCjServiceImpl.class);

    @Autowired()
    private DdjKhCjMapper ddjkhcjDao;
    
    
	@Override
	public void save(DdjKhCj ddjkhcj) {
		Date gltime = ddjkhcj.getGltime();
		if(null==gltime){
			ddjkhcj.setGltime(DateUtils.getDate());
		}
		ddjkhcjDao.insert(ddjkhcj);
	}

	@Override
	public void deleteById(DdjKhCj ddjkhcj) {
		// TODO Auto-generated method stub
		ddjkhcjDao.deleteByPrimaryKey(ddjkhcj);
	}

	@Override
	public void updateById(DdjKhCj ddjkhcj) {
		// TODO Auto-generated method stub

	}

	@Override
	public long count(PageRequest pageRequest) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DdjKhCj getById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<DdjKhCj> findPage(PageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateValuesById(DdjKhCj ddjkhcj) {
		ddjkhcjDao.updateByPrimaryKey(ddjkhcj);

	}

	@Override
	public List<DdjKhCj> getByValues(DdjKhCj ddjkhcj) {
		
		return ddjkhcjDao.getByValues(ddjkhcj);
	}

	@Override
	public void saveOrUpdateById(DdjKhCj ddjkhcj) {
		if(ddjkhcj.getUseridCj()!=0&&ddjkhcj.getUseridKh()!=0){
			this.updateValuesById(ddjkhcj);
		}
		this.save(ddjkhcj);

	}

	@Override
	public void saveList(List<DdjKhCj> ddjkhcjList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteByIds(List<String> ids) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getCompnayIdByValues(DdjKhCj ddjkhcj) {
		// TODO Auto-generated method stub
		return ddjkhcjDao.getCompnayIdByValues(ddjkhcj);
	}

}
