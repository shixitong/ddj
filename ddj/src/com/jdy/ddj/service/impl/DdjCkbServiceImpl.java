package com.jdy.ddj.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jdy.ddj.common.dao.RequestBean;
import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.dao.DdjCkbMapper;
import com.jdy.ddj.entity.DdjCkb;
import com.jdy.ddj.service.DdjCkbService;


@Service("ckbService")
@Transactional(rollbackFor = {Exception.class,RuntimeException.class})
public class DdjCkbServiceImpl implements DdjCkbService {

	
	@Autowired()
	private DdjCkbMapper ddjckbDao;
	
	@Override
	public void save(DdjCkb ddjckb) {
		ddjckb.setStatus((short)1);
		ddjckbDao.insert(ddjckb);
	}

	@Override
	public void deleteById(String id) {
		ddjckbDao.deleteByPrimaryKey(Long.valueOf(id));

	}

	@Override
	public void updateById(DdjCkb ddjckb) {
		// TODO Auto-generated method stub

	}

	@Override
	public long count(PageRequest pageRequest) {
		 RequestBean requestBean = RequestBean.request(pageRequest);
	     requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
	     return ddjckbDao.count(requestBean);
	}

	@Override
	public DdjCkb getById(String id) {
		
		return ddjckbDao.selectByPrimaryKey(Long.valueOf(id));
	}

	@Override
	public Page<DdjCkb> findPage(PageRequest pageRequest) {
		RequestBean requestBean = RequestBean.request(pageRequest);
	    requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
	    return requestBean.getPage(ddjckbDao.findPage(requestBean));
	}

	@Override
	public void updateValuesById(DdjCkb ddjckb) {
		ddjckbDao.updateByPrimaryKeySelective(ddjckb);
	}

	@Override
	public List<DdjCkb> getByValues(DdjCkb ddjckb) {
		return ddjckbDao.getByValues(ddjckb);
	}

	@Override
	public void saveOrUpdateById(DdjCkb ddjckb) {
		if(ddjckb.getCkid()>0){
			this.updateValuesById(ddjckb);
		}
		else{
			this.save(ddjckb);
		}

	}

	@Override
	public void saveList(List<DdjCkb> ddjckbList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteByIds(List<String> ids) {
		// TODO Auto-generated method stub

	}

}
