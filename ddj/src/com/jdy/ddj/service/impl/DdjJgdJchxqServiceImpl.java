package com.jdy.ddj.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.common.utils.DateUtils;
import com.jdy.ddj.dao.DdjJgdJchxqMapper;
import com.jdy.ddj.entity.DdjJgdJchxq;
import com.jdy.ddj.service.DdjJgdJchxqService;


@Service("jgdjchxqService")
@Transactional(rollbackFor = {Exception.class,RuntimeException.class})
public class DdjJgdJchxqServiceImpl implements DdjJgdJchxqService {

	
	@Autowired()
	private DdjJgdJchxqMapper jgdjchxqDao;
	@Override
	public void save(DdjJgdJchxq ddjJgdJchxq) {

		if(null==ddjJgdJchxq.getQrtime()){
			ddjJgdJchxq.setQrtime(DateUtils.getDate());
		}
		jgdjchxqDao.insert(ddjJgdJchxq);
		
	}

	@Override
	public void deleteById(String id) {
		jgdjchxqDao.deleteByPrimaryKey(Long.valueOf(id));
	}

	@Override
	public void updateById(DdjJgdJchxq ddjJgdJchxq) {
		jgdjchxqDao.updateByPrimaryKey(ddjJgdJchxq);
	}

	@Override
	public long count(PageRequest pageRequest) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DdjJgdJchxq getById(String id) {
		// TODO Auto-generated method stub
		return jgdjchxqDao.selectByPrimaryKey(Long.valueOf(id));
	}

	@Override
	public Page<DdjJgdJchxq> findPage(PageRequest pageRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateValuesById(DdjJgdJchxq ddjJgdJchxq) {
		jgdjchxqDao.updateByPrimaryKeySelective(ddjJgdJchxq);

	}

	@Override
	public List<DdjJgdJchxq> getByValues(DdjJgdJchxq ddjJgdJchxq) {
		return jgdjchxqDao.getByValues(ddjJgdJchxq);
	}

	@Override
	public void saveOrUpdateById(DdjJgdJchxq ddjJgdJchxq) {
		if(ddjJgdJchxq.getGdid()!=null&&!"".equals(ddjJgdJchxq.getGdid())){
			this.updateValuesById(ddjJgdJchxq);
		}
		else{
			this.save(ddjJgdJchxq);
		}
		
	}

	@Override
	public void saveList(List<DdjJgdJchxq> ddjJgdJchxqList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteByIds(List<String> ids) {
		// TODO Auto-generated method stub

	}

}
