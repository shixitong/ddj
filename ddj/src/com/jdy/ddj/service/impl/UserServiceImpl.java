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
import com.jdy.ddj.dao.DdjUserMapper;
import com.jdy.ddj.entity.DdjUser;
import com.jdy.ddj.service.UserService;

@Service("userService")
@Transactional(rollbackFor = {Exception.class,RuntimeException.class})
public class UserServiceImpl implements UserService {

	//日志
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired()
    private DdjUserMapper ddjUserDao;
	    
	/* (non-Javadoc)
	 * @see com.jdy.ddj.service.UserService#save(com.jdy.ddj.entity.DdjUser)
	 * 保存用户
	 */
	@Override
	public void save(DdjUser ddjUser) {
		//用户名和手机号码
		//TODO 其它相应的判断根据需要添加
		String username=ddjUser.getUsername();
		String telephone=ddjUser.getTelephone();
		if(null!=username&&!username.equals("")&&!telephone.equals("")&&!telephone.equals("")){
			ddjUserDao.insert(ddjUser);
		}
	}

	@Override
	public void deleteById(String id) {
		ddjUserDao.deleteByPrimaryKey(Long.valueOf(id));
	}

	@Override
	public void updateById(DdjUser ddjUser) {

	}

	@Override
	public long count(PageRequest pageRequest) {
		 RequestBean requestBean = RequestBean.request(pageRequest);
	     requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
	     return ddjUserDao.count(requestBean);
	}

	@Override
	public DdjUser getById(String id) {
		return ddjUserDao.selectByPrimaryKey(Long.valueOf(id));
	}

	@Override
	public Page<DdjUser> findPage(PageRequest pageRequest) {
		RequestBean requestBean = RequestBean.request(pageRequest);
	    requestBean.createCriteria().addCriterion(pageRequest.getCriteria());
	    return requestBean.getPage(ddjUserDao.findPage(requestBean));
	}

	@Override
	public void updateValuesById(DdjUser ddjUser) {
		ddjUserDao.updateByPrimaryKeySelective(ddjUser);
	}

	@Override
	public List<DdjUser> getByValues(DdjUser ddjUser) {
		return ddjUserDao.getByValues(ddjUser);
	}

	@Override
	public void saveOrUpdateById(DdjUser ddjUser) {
		
		 if(ddjUser.getUserid()!=null&&!ddjUser.getUserid().equals("")){
		        this.updateValuesById(ddjUser);
		    }else{
		        this.save(ddjUser);
		    }
	}

	@Override
	public void saveList(List<DdjUser> ddjUserList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteByIds(List<String> ids) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<DdjUser> getByIds(String ids) {
		// TODO Auto-generated method stub
		return ddjUserDao.getByIds(ids);
	}

}
