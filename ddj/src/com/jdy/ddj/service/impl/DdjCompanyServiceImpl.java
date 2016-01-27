package com.jdy.ddj.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jdy.ddj.common.dao.RequestBean;
import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.dao.DdjCompanyMapper;
import com.jdy.ddj.entity.DdjCompany;
import com.jdy.ddj.service.DdjCompanyService;

@Service("companyService")
@Transactional(rollbackFor = {Exception.class,RuntimeException.class})
public class DdjCompanyServiceImpl implements DdjCompanyService {

	
	//日志
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired()
	private DdjCompanyMapper ddjCompanyDao;
	    
	@Override
	public void save(DdjCompany ddjCompany) {
		String companyname=ddjCompany.getCompanyname();
		if(null!=companyname&&!companyname.equals("")){
			ddjCompanyDao.insert(ddjCompany);
		}

	}

	@Override
	public void deleteById(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateById(DdjCompany ddjCompany) {
		// TODO Auto-generated method stub

	}

	@Override
	public long count(PageRequest pageRequest) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public void saveOrUpdateById(DdjCompany ddjCompany) {
		 if(ddjCompany.getCompanyid()>0){
		        this.updateValuesById(ddjCompany);
		    }else{
		        this.save(ddjCompany);
		    }

	}

	@Override
	public void saveList(List<DdjCompany> ddjCompanyList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteByIds(List<String> ids) {
		// TODO Auto-generated method stub

	}

	@Override
	public DdjCompany getById(String id) {
		// TODO Auto-generated method stub
		return ddjCompanyDao.selectByPrimaryKey(Long.valueOf(id));
	}

	@Override
	public Page<DdjCompany> findPage(PageRequest pageRequest) {
		RequestBean requestBean = RequestBean.request(pageRequest);
	    requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
	    return requestBean.getPage(ddjCompanyDao.findPage(requestBean));
	}

	@Override
	public void updateValuesById(DdjCompany ddjCompany) {
		ddjCompanyDao.updateValuesById(ddjCompany);
	}

	@Override
	public List<DdjCompany> getByValues(DdjCompany ddjCompany) {
		return ddjCompanyDao.getByValues(ddjCompany);
	}

	@Override
	public List<DdjCompany> getJgdCjList(long useridKh) {
		return ddjCompanyDao.getJgdCjList(useridKh);
	}

	@Override
	public List<DdjCompany> getJgdKhList(long useridCj) {
		return ddjCompanyDao.getJgdKhList(useridCj);
	}

}
