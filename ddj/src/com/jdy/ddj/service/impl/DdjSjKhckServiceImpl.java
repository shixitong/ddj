package com.jdy.ddj.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.common.utils.DateUtils;
import com.jdy.ddj.dao.DdjSjKhckMapper;
import com.jdy.ddj.entity.DdjSjKhck;
import com.jdy.ddj.service.DdjSjKhckService;


@Service("sjkhckService")
@Transactional(rollbackFor = {Exception.class,RuntimeException.class})
public class DdjSjKhckServiceImpl implements DdjSjKhckService {

	
	@Autowired()
	private DdjSjKhckMapper ddjsjkhckDao;
	 
	 
	@Override
	public void save(DdjSjKhck ddjsjkhck) {

		Date glsj = ddjsjkhck.getGlsj();
		if(null==glsj){
			ddjsjkhck.setGlsj(DateUtils.getDate());
		}
		ddjsjkhckDao.insert(ddjsjkhck);
	}

	@Override
	public void deleteById(DdjSjKhck key) {
		ddjsjkhckDao.deleteByPrimaryKey(key);
	}

	@Override
	public void updateById(DdjSjKhck ddjsjkhck) {
		ddjsjkhckDao.updateByPrimaryKey(ddjsjkhck);
	}

	@Override
	public long count(PageRequest pageRequest) {
		return 0;
	}

	@Override
	public DdjSjKhck getById(String id) {
		return null;
	}

	@Override
	public Page<DdjSjKhck> findPage(PageRequest pageRequest) {
		return null;
	}

	@Override
	public void updateValuesById(DdjSjKhck ddjsjkhck) {
		ddjsjkhckDao.updateByPrimaryKeySelective(ddjsjkhck);
	}

	@Override
	public List<DdjSjKhck> getByValues(DdjSjKhck ddjsjkhck) {
		return ddjsjkhckDao.getByValues(ddjsjkhck);
	}

	@Override
	public void saveOrUpdateById(DdjSjKhck ddjsjkhck) {
		if(ddjsjkhck.getSjbh()>0&&ddjsjkhck.getCkbh()>0){
			ddjsjkhckDao.updateByPrimaryKeySelective(ddjsjkhck);
		}
		else{
			this.save(ddjsjkhck);
		}

	}

	@Override
	public void saveList(List<DdjSjKhck> ddjsjkhckList) {

	}

	@Override
	public void deleteByIds(List<String> ids) {

	}

}
