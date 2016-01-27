package com.jdy.ddj.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jdy.ddj.common.dao.RequestBean;
import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.common.utils.DateUtils;
import com.jdy.ddj.dao.DdjBjdMapper;
import com.jdy.ddj.entity.DdjBjd;
import com.jdy.ddj.service.DdjBjdService;

@Service("bjdService")
@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
public class DdjBjdServiceImpl implements DdjBjdService {

	@Autowired()
	private DdjBjdMapper ddjbjdDao;

	@Override
	public void save(DdjBjd ddjbjd) {
		Date gltime = ddjbjd.getBjtime();
		if (null == gltime) {
			ddjbjd.setBjtime(DateUtils.getDate());
		}
		ddjbjdDao.insert(ddjbjd);

	}

	@Override
	public void deleteById(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateById(DdjBjd ddjbjd) {
		// TODO Auto-generated method stub

	}

	@Override
	public long count(PageRequest pageRequest) {
		RequestBean requestBean = RequestBean.request(pageRequest);
		requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
		return ddjbjdDao.count(requestBean);
	}

	@Override
	public DdjBjd getById(String id) {
		// TODO Auto-generated method stub
		return ddjbjdDao.selectByPrimaryKey(Long.valueOf(id));
	}

	@Override
	public Page<DdjBjd> findPage(PageRequest pageRequest) {
		RequestBean requestBean = RequestBean.request(pageRequest);
		requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
		return requestBean.getPage(ddjbjdDao.findPage(requestBean));
	}

	@Override
	public void updateValuesById(DdjBjd ddjbjd) {
		// TODO Auto-generated method stub
		ddjbjdDao.updateByPrimaryKeySelective(ddjbjd);
	}

	@Override
	public List<DdjBjd> getByValues(DdjBjd ddjbjd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdateById(DdjBjd ddjbjd) {
		if (ddjbjd.getYwid() != 0) {
			this.updateValuesById(ddjbjd);
		} else {
			this.save(ddjbjd);
		}

	}

	@Override
	public void saveList(List<DdjBjd> ddjbjdList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteByIds(List<String> ids) {
		// TODO Auto-generated method stub

	}

}
