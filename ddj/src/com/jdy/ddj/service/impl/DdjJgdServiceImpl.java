package com.jdy.ddj.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jdy.ddj.common.dao.RequestBean;
import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.common.utils.DateUtils;
import com.jdy.ddj.dao.DdjJgdMapper;
import com.jdy.ddj.entity.DdjJgd;
import com.jdy.ddj.service.DdjJgdService;
import com.jdy.ddj.utils.GenerateSequenceUtil;


@Service("jgdService")
@Transactional(rollbackFor = {Exception.class,RuntimeException.class})
public class DdjJgdServiceImpl implements DdjJgdService {

	
	@Autowired()
	private DdjJgdMapper ddjjgdDao;
	
	
	@Override
	public void save(DdjJgd ddjjgd) {
		String gdid=GenerateSequenceUtil.generateSequenceNo();
		ddjjgd.setGdid("JGD"+gdid);
		Date gltime = ddjjgd.getCssj();
		if(null==gltime){
			ddjjgd.setCssj(DateUtils.getDate());
		}
		ddjjgdDao.insert(ddjjgd);
		
	}

	@Override
	public void deleteById(String id) {

		ddjjgdDao.deleteByPrimaryKey(id);
	}

	@Override
	public void updateById(DdjJgd ddjjgd) {
		ddjjgdDao.updateByPrimaryKey(ddjjgd);
	}

	@Override
	public long count(PageRequest pageRequest) {
		RequestBean requestBean = RequestBean.request(pageRequest);
		requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
		return ddjjgdDao.count(requestBean);
	}

	@Override
	public DdjJgd getById(String id) {
		return ddjjgdDao.selectByPrimaryKey(id);
	}

	@Override
	public Page<DdjJgd> findPage(PageRequest pageRequest) {
		RequestBean requestBean = RequestBean.request(pageRequest);
		requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
		return requestBean.getPage(ddjjgdDao.findPage(requestBean));
	}

	@Override
	public void updateValuesById(DdjJgd ddjjgd) {
		ddjjgdDao.updateByPrimaryKeySelective(ddjjgd);
	}

	@Override
	public List<DdjJgd> getByValues(DdjJgd ddjjgd) {
		return this.ddjjgdDao.getByValues(ddjjgd);
	}

	@Override
	public void saveOrUpdateById(DdjJgd ddjjgd) {
		
		System.out.println(ddjjgd);
		if(ddjjgd.getGdid()!=null&&!"".equals(ddjjgd.getGdid())){
			this.updateValuesById(ddjjgd);
		}
		else{
			this.save(ddjjgd);
		}
	}

	@Override
	public void saveList(List<DdjJgd> ddjjgdList) {

	}

	@Override
	public void deleteByIds(List<String> ids) {

	}

	@Override
	public List<Map> getTotalStat(DdjJgd ddjjgd) {
		return ddjjgdDao.getTotalStat(ddjjgd);
	}

}
