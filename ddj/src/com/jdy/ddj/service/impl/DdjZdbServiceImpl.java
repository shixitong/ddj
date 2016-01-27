package com.jdy.ddj.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jdy.ddj.common.dao.RequestBean;
import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.common.utils.DateUtils;
import com.jdy.ddj.dao.DdjZdbMapper;
import com.jdy.ddj.entity.DdjZdb;
import com.jdy.ddj.service.DdjZdbService;

/**
 * @author Administrator
 *
 */
@Service("zdbService")
@Transactional(rollbackFor = {Exception.class,RuntimeException.class})
public class DdjZdbServiceImpl implements DdjZdbService {

    @Autowired()
    private DdjZdbMapper zdbdao;
	
	@Override
	public void save(DdjZdb ddjzdb) {
		ddjzdb.setCreatetime(DateUtils.getDate());
		zdbdao.insert(ddjzdb);
	}

	@Override
	public void deleteById(String id) {

		zdbdao.deleteByPrimaryKey(Long.valueOf(id));
	}

	@Override
	public void updateById(DdjZdb ddjzdb) {
		zdbdao.updateByPrimaryKey(ddjzdb);
	}

	@Override
	public long count(PageRequest pageRequest) {
		RequestBean requestBean = RequestBean.request(pageRequest);
		requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
		return zdbdao.count(requestBean);
	}

	@Override
	public DdjZdb getById(String id) {
		return zdbdao.selectByPrimaryKey(Long.valueOf(id));
	}

	@Override
	public Page<DdjZdb> findPage(PageRequest pageRequest) {
		RequestBean requestBean = RequestBean.request(pageRequest);
		requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
		return requestBean.getPage(zdbdao.findPage(requestBean));
	}

	@Override
	public void updateValuesById(DdjZdb ddjzdb) {

		zdbdao.updateByPrimaryKeySelective(ddjzdb);
	}

	@Override
	public List<DdjZdb> getByValues(DdjZdb ddjzdb) {
		// TODO Auto-generated method stub
		return zdbdao.getByValues(ddjzdb);
	}

	@Override
	public void saveOrUpdateById(DdjZdb ddjzdb) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveList(List<DdjZdb> ddjzdbList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteByIds(List<String> ids) {
		// TODO Auto-generated method stub

	}

}
