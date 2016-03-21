package com.jdy.ddj.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jdy.ddj.common.dao.Criteria;
import com.jdy.ddj.common.dao.Criterion;
import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.common.utils.DateUtils;
import com.jdy.ddj.common.utils.HafProperties;
import com.jdy.ddj.entity.DdjBjd;
import com.jdy.ddj.entity.DdjCkb;
import com.jdy.ddj.entity.DdjCompany;
import com.jdy.ddj.entity.DdjJgd;
import com.jdy.ddj.entity.DdjJgdJchxq;
import com.jdy.ddj.entity.DdjKhCj;
import com.jdy.ddj.entity.DdjSjKhck;
import com.jdy.ddj.entity.DdjUser;
import com.jdy.ddj.entity.DdjZdb;
import com.jdy.ddj.entity.Official;
import com.jdy.ddj.service.DdjBjdService;
import com.jdy.ddj.service.DdjCkbService;
import com.jdy.ddj.service.DdjCompanyService;
import com.jdy.ddj.service.DdjJgdJchxqService;
import com.jdy.ddj.service.DdjJgdService;
import com.jdy.ddj.service.DdjKhCjService;
import com.jdy.ddj.service.DdjSjKhckService;
import com.jdy.ddj.service.DdjZdbService;
import com.jdy.ddj.service.OfficialService;
import com.jdy.ddj.service.UserService;
import com.jdy.ddj.utils.Arith;
import com.jdy.ddj.utils.CommonResult;
import com.jdy.ddj.utils.ResManager;
import com.jdy.ddj.utils.XboxUtils;

@Controller
public class UserController {

	// 日志
	private Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private DdjCompanyService companyService;

	@Autowired
	private DdjKhCjService ddjkhcjService;

	@Autowired
	private DdjBjdService bjdService;

	@Autowired
	private DdjCkbService ckbService;

	@Autowired
	private DdjJgdService jgdService;

	@Autowired
	private DdjJgdJchxqService jgdjchxqService;

	@Autowired
	private DdjSjKhckService sjkhckService;

	@Autowired
	private OfficialService officialService;

	@Autowired
	private DdjZdbService zdbService;

	/***
	 * 注册
	 * 
	 * @param response
	 * @param ddjUser
	 */
	@RequestMapping("register.do")
	public void register(HttpServletResponse response, DdjCompany ddjCompany, DdjUser ddjUser) {

		CommonResult commonResult = new CommonResult();
		try {
			if (StringUtils.isNotEmpty(ddjUser.getTelephone()) && StringUtils.isNotEmpty(ddjUser.getPassword())) {

				// 获得用户所属公司编号
				long companyid = 0l;
				DdjCompany company = new DdjCompany();
				company.setCompanyname(ddjCompany.getCompanyname());
				// 根据公司判断该公司是否存在
				List<DdjCompany> ddjCompanyList = companyService.getByValues(company);
				if (null != ddjCompanyList && ddjCompanyList.size() > 0) {
					// 存在
					companyid = ddjCompanyList.get(0).getCompanyid();
				} else {
					// 不存在先保存
					companyService.save(ddjCompany);
					List<DdjCompany> ddjCompanyList1 = companyService.getByValues(ddjCompany);
					if (null != ddjCompanyList1 && ddjCompanyList1.size() > 0) {
						companyid = ddjCompanyList1.get(0).getCompanyid();

					}
				}
				// 设置用户的公司编码
				ddjUser.setCompanyid(companyid);

				// 判断注册的手机号是否存在
				DdjUser user = new DdjUser();
				user.setTelephone(ddjUser.getTelephone());
				List<DdjUser> ddjUserList = userService.getByValues(user);
				if (ddjUserList != null && ddjUserList.size() > 0) {
					commonResult.setCode(XboxUtils.STATUS_ERROR);
					commonResult.setMessage("手机号已经存在");
				} else {
					short usertype = ddjUser.getUsertype();
					// 当用户为客户或者厂家的时候设置需要审核
					if (usertype == 1 || usertype == 2) {
						ddjUser.setAudit((short) 0);
					} else {
						ddjUser.setAudit((short) 1);
					}
					ddjUser.setStatus((short) 1);
					userService.saveOrUpdateById(ddjUser);
				}
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("手机号码不能为空");
			}
		} catch (Exception e) {
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			ResManager.getOut(response).print(new JSONObject(commonResult));
			ResManager.close();
		}
	}

	/**
	 * 用户登录
	 * 
	 * @param request
	 * @param response
	 * @param userphone
	 * @param password
	 */
	@RequestMapping("/login.do")
	public void login(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "userphone", required = false) String userphone,
			@RequestParam(value = "password", required = false) String password) {
		CommonResult commonResult = new CommonResult();
		try {
			if (StringUtils.isNotEmpty(userphone) && StringUtils.isNotEmpty(password)) {
				DdjUser ddjUser = new DdjUser();
				ddjUser.setTelephone(userphone);
				ddjUser.setPassword(password);
				ddjUser.setStatus((short) 1);
				List<DdjUser> clientList = userService.getByValues(ddjUser);

				if (clientList != null && clientList.size() > 0) {
					// 登录成功
					DdjUser user = clientList.get(0);

					if (user.getAudit() == 0) {
						commonResult.setCode(XboxUtils.STATUS_ERROR);
						commonResult.setMessage("请等待平台审核");
					} else {
						JSONObject jsonObject = new JSONObject(user);
						// 客户
						if (user.getUsertype() == 1) {
							DdjCkb ckb = new DdjCkb();
							ckb.setUserid(user.getUserid());
							List<DdjCkb> ckbList = ckbService.getByValues(ckb);
							if (null != ckbList && ckbList.size() > 0) {
								jsonObject.put("ckid", ckbList.get(0).getCkid());
								jsonObject.put("ckaddress", ckbList.get(0).getCkaddress());
							}
						}
						// 厂家
						if (user.getUsertype() == 2) {
							DdjCkb ckb = new DdjCkb();
							ckb.setUserid(user.getUserid());
							List<DdjCkb> ckbList = ckbService.getByValues(ckb);
							if (null != ckbList && ckbList.size() > 0) {
								jsonObject.put("ckid", ckbList.get(0).getCkid());
								jsonObject.put("ckaddress", ckbList.get(0).getCkaddress());
							}
						}
						// 厂家仓库
						if (user.getUsertype() == 3) {
							long currentUserid = user.getUserid();
							DdjUser lxrUser = userService.getById("" + currentUserid);
							long companyid = lxrUser.getCompanyid();

							long cjid = 0l;
							DdjUser cjUser = new DdjUser();
							cjUser.setCompanyid(companyid);
							cjUser.setUsertype((short) 2);
							List<DdjUser> cjUserList = userService.getByValues(cjUser);
							if (null != cjUserList && cjUserList.size() > 0) {
								cjid = cjUserList.get(0).getUserid();
								jsonObject.put("cjbh", cjid);
							}
							DdjCkb ckb = new DdjCkb();
							ckb.setCklxrid(currentUserid);
							List<DdjCkb> ckbList = ckbService.getByValues(ckb);
							if (null != ckbList && ckbList.size() > 0) {
								jsonObject.put("ckid", ckbList.get(0).getCkid());
								jsonObject.put("ckaddress", ckbList.get(0).getCkaddress());
							}
						}
						// 客户仓库
						if (user.getUsertype() == 4) {
							long currentUserid = user.getUserid();
							DdjUser lxrUser = userService.getById("" + currentUserid);
							long companyid = lxrUser.getCompanyid();

							long khid = 0l;
							DdjUser khUser = new DdjUser();
							khUser.setCompanyid(companyid);
							khUser.setUsertype((short) 1);
							List<DdjUser> khUserList = userService.getByValues(khUser);
							if (null != khUserList && khUserList.size() > 0) {
								khid = khUserList.get(0).getUserid();
								jsonObject.put("khbh", khid);
							}

							DdjCkb ckb = new DdjCkb();
							ckb.setCklxrid(currentUserid);
							List<DdjCkb> ckbList = ckbService.getByValues(ckb);
							if (null != ckbList && ckbList.size() > 0) {

								jsonObject.put("ckid", ckbList.get(0).getCkid());
								jsonObject.put("ckaddress", ckbList.get(0).getCkaddress());
							}
						}
						// 司机
						if (user.getUsertype() == 5) {

						}
						commonResult.setResult(jsonObject);
					}
				} else {

					commonResult.setCode(XboxUtils.STATUS_ERROR);
					commonResult.setMessage("用户或者密码错误");
				}
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("用户名或者密码为空");
			}
			ResManager.getOut(response).print(commonResult.getJsonObject());
		} catch (Exception e) {
			e.printStackTrace();
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			logger.error(e.getMessage());
		} finally {
			ResManager.close();
		}
	}

	/**
	 * 验证手机号是否存在
	 * 
	 * @param response
	 * @param userphone
	 */
	@RequestMapping("vfPhone.do")
	public void vfPhone(HttpServletResponse response,
			@RequestParam(value = "userphone", required = false) String userphone) {
		CommonResult commonResult = new CommonResult();
		// 获取输入
		try {
			if (StringUtils.isNotEmpty(userphone)) {
				DdjUser ddjUser = new DdjUser();
				ddjUser.setTelephone(userphone);
				List<DdjUser> ddjUserList = userService.getByValues(ddjUser);
				if (ddjUserList != null && ddjUserList.size() > 0) {
					commonResult.setMessage("手机号存在！");
				} else {
					commonResult.setCode(XboxUtils.STATUS_ERROR);
					commonResult.setMessage("手机号不存在！");
				}
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("手机号不能为空！");
			}
		} catch (Exception e) {
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(new JSONObject(commonResult));
			ResManager.close();
		}
	}

	/**
	 * 忘记密码
	 * 
	 * @param response
	 * @param password
	 * @param userphone
	 */
	@RequestMapping("findPassword.do")
	public void findPassword(HttpServletResponse response,
			@RequestParam(value = "password", required = false) String password,
			@RequestParam(value = "userphone", required = false) String userphone) {

		CommonResult commonResult = new CommonResult();
		// 获取输入
		try {
			if (StringUtils.isNotEmpty(userphone) && StringUtils.isNotEmpty(password)) {
				// 客户
				DdjUser ddjUser = new DdjUser();
				ddjUser.setTelephone(userphone);
				List<DdjUser> ddjUserList = userService.getByValues(ddjUser);
				if (ddjUserList != null && ddjUserList.size() > 0) {
					ddjUser = ddjUserList.get(0);
					ddjUser.setPassword(password);
					userService.updateValuesById(ddjUser);
				} else {
					commonResult.setCode(XboxUtils.STATUS_ERROR);
					commonResult.setMessage("手机号不存在！");
				}
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
			}
		} catch (Exception e) {
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(new JSONObject(commonResult));
			ResManager.close();
		}
	}

	/***
	 * 修改密码
	 * 
	 * @param response
	 * @param userphone
	 * @param password
	 */
	@RequestMapping("alterPassword.do")
	public void findPassword(HttpServletResponse response,
			@RequestParam(value = "telephone", required = false) String telephone,
			@RequestParam(value = "oldPassword", required = false) String oldPassword,
			@RequestParam(value = "password", required = false) String password) {

		CommonResult commonResult = new CommonResult();
		// 获取输入
		try {
			if (StringUtils.isNotEmpty(telephone) && StringUtils.isNotEmpty(password)
					&& StringUtils.isNotEmpty(oldPassword)) {

				// 客户
				DdjUser ddjUser = new DdjUser();
				ddjUser.setTelephone(telephone);
				List<DdjUser> clientList = userService.getByValues(ddjUser);
				if (clientList != null && clientList.size() > 0) {
					ddjUser = clientList.get(0);
					if (oldPassword.equals(ddjUser.getPassword())) {
						ddjUser.setPassword(password);
						userService.updateValuesById(ddjUser);
					} else {
						commonResult.setCode(XboxUtils.STATUS_ERROR);
						commonResult.setMessage("原密码输入错误！");
					}
				} else {
					commonResult.setCode(XboxUtils.STATUS_ERROR);
					commonResult.setMessage("手机号不存在！");
				}
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
			}
		} catch (Exception e) {
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			ResManager.getOut(response).print(new JSONObject(commonResult));
			ResManager.close();
		}
	}

	/**
	 * 客户厂家关联
	 * 
	 * @param response
	 * @param useridKh
	 * @param useridCj
	 */
	@RequestMapping("corperateCj.do")
	public void corperateCj(HttpServletResponse response,
			@RequestParam(value = "useridKh", required = true) long useridKh,
			@RequestParam(value = "useridCj", required = true) long useridCj) {
		CommonResult commonResult = new CommonResult();
		try {
			DdjKhCj ddjkhcj = new DdjKhCj();
			ddjkhcj.setUseridCj(useridCj);
			ddjkhcj.setUseridKh(useridKh);
			List<DdjKhCj> clientList = ddjkhcjService.getByValues(ddjkhcj);
			if (clientList != null && clientList.size() > 0) {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("关联关系已经存在！");
			} else {
				ddjkhcjService.saveOrUpdateById(ddjkhcj);
			}
		} catch (Exception e) {
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			ResManager.getOut(response).print(new JSONObject(commonResult));
			ResManager.close();
		}

	}

	/**
	 * 客户取消和厂家合作
	 * 
	 * @param response
	 * @param useridKh
	 * @param useridCj
	 */
	@RequestMapping("cancleCj.do")
	public void cancleCj(HttpServletResponse response, @RequestParam(value = "useridKh", required = true) long useridKh,
			@RequestParam(value = "useridCj", required = true) long useridCj) {
		CommonResult commonResult = new CommonResult();
		try {
			DdjKhCj ddjkhcj = new DdjKhCj();
			ddjkhcj.setUseridCj(useridCj);
			ddjkhcj.setUseridKh(useridKh);
			List<DdjKhCj> clientList = ddjkhcjService.getByValues(ddjkhcj);
			if (clientList != null && clientList.size() > 0) {
				ddjkhcjService.deleteById(clientList.get(0));

			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("合作关系不存在！");
			}
		} catch (Exception e) {
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			ResManager.getOut(response).print(new JSONObject(commonResult));
			ResManager.close();
		}
	}

	/**
	 * 获取合作与全部厂家列表
	 * 
	 * @param response
	 * @param userphone
	 * @throws Exception
	 */
	@RequestMapping("/getVender.do")
	public void getVender(HttpServletResponse response, int page, int rows,
			@RequestParam(value = "useridKh", required = false) String useridKh,
			@RequestParam(value = "cityName", required = false) String cityName) throws Exception {

		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();

		try {

			StringBuffer sbf1 = new StringBuffer();
			// 如果用户编码不为空
			if (StringUtils.isNotEmpty(useridKh)) {
				DdjKhCj ddjkhcj = new DdjKhCj();
				ddjkhcj.setUseridKh(Long.valueOf(useridKh));
				// 合作关系
				List<String> userList = ddjkhcjService.getCompnayIdByValues(ddjkhcj);

				if (null != userList) {
					for (int i = 0; i < userList.size(); i++) {
						String companyid = userList.get(i);
						if (i != userList.size() - 1) {
							sbf1.append(companyid + ",");
						} else {
							sbf1.append(companyid);
						}
					}

				} else {

				}
			}

			PageRequest pageRequest = new PageRequest();
			pageRequest.setPageSize(rows);
			pageRequest.setPageNo(page);
			Criteria criteria = new Criteria();
			if (StringUtils.isNotEmpty(useridKh)) {
				if (StringUtils.isNotEmpty(sbf1.toString())) {
					criteria.addCriterion(new Criterion("  companyid in (" + sbf1.toString() + ")  "));
				} else {
					criteria.addCriterion(new Criterion("  1=2  "));
				}
			}

			if (StringUtils.isNotEmpty(cityName)) {
				criteria.addCriterion(new Criterion("  address like '%" + cityName + "%'"));

			}
			criteria.addCriterion(
					new Criterion("  companyid  in (select ddj_user.companyid from ddj_user where usertype=2) "));
			pageRequest.getCriteria().addAll(criteria.getAllCriteria());
			Page<DdjCompany> ddjCompanys = companyService.findPage(pageRequest);
			// 输出json对象
			JSONObject json = new JSONObject();
			JSONArray array = new JSONArray();
			if (ddjCompanys != null && ddjCompanys.getResult() != null && ddjCompanys.getResult().size() > 0) {
				for (DdjCompany ddjCompany : ddjCompanys.getResult()) {
					JSONObject jsonObject = new JSONObject();
					DdjUser queryUser1 = new DdjUser();
					queryUser1.setCompanyid(ddjCompany.getCompanyid());
					queryUser1.setUsertype((short) (2));
					long useridCj = 0l;
					List<DdjUser> cjUser = userService.getByValues(queryUser1);
					if (null != cjUser && cjUser.size() > 0) {
						useridCj = cjUser.get(0).getUserid();
						jsonObject.put("companyid", ddjCompany.getCompanyid());
						jsonObject.put("useridCj", useridCj);
						jsonObject.put("name", ddjCompany.getCompanyname());
						jsonObject.put("logo", ddjCompany.getLogo() == null ? "" : ddjCompany.getLogo());
						jsonObject.put("address", ddjCompany.getAddress());
						jsonObject.put("phone", ddjCompany.getPhone());
						jsonObject.put("pfen", ddjCompany.getPfen() + "");
						array.put(jsonObject);
					} else {
						// 说明没有找到对应的厂家
					}

				}
			}
			json.put("myList", array);
			result.put("result", json);

		} catch (Exception e) {
			logger.error(e.getMessage());
			result.put("code", XboxUtils.STATUS_ERROR);
			result.put("message", " 系统错误");
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}

	}
	
	
	
	/**
	 * 获取有工单来往的商家列表
	 * @param response
	 * @param userphone
	 * @throws Exception
	 */
	@RequestMapping("/getSjList.do")
	public void getSjList(HttpServletResponse response,
			@RequestParam(value = "useridKh", required = false) String useridKh,
			@RequestParam(value = "cityName", required = false) String cityName) throws Exception {

		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();

		try {
			// 如果用户编码不为空
			if (StringUtils.isNotEmpty(useridKh)) {
				List<DdjCompany> companyList=companyService.getJgdCjList(Long.valueOf(useridKh));
				// 输出json对象
				JSONObject json = new JSONObject();
				JSONArray array = new JSONArray();
				if (companyList != null && companyList.size() > 0) {
					for (DdjCompany ddjCompany : companyList) {
						JSONObject jsonObject = new JSONObject();
						DdjUser queryUser1 = new DdjUser();
						queryUser1.setCompanyid(ddjCompany.getCompanyid());
						queryUser1.setUsertype((short) (2));
						queryUser1.setStatus((short) (1));
						List<DdjUser> cjUser = userService.getByValues(queryUser1);
						if (null != cjUser && cjUser.size() > 0) {
							long useridCj1 = cjUser.get(0).getUserid();
							String username = cjUser.get(0).getUsername();
							jsonObject.put("userid", useridCj1);
							jsonObject.put("username", username);
							jsonObject.put("companyid", ddjCompany.getCompanyid());
							jsonObject.put("companyname", ddjCompany.getCompanyname());
							jsonObject.put("logo", ddjCompany.getLogo() == null ? "" : ddjCompany.getLogo());
							jsonObject.put("address", ddjCompany.getAddress());
							jsonObject.put("phone", ddjCompany.getPhone());
							array.put(jsonObject);
						} else {
							// 说明没有找到对应的客户
						}
					}
				}
				json.put("myList", array);
				result.put("result", json);
			}
			else{
				result.put("code", XboxUtils.STATUS_ERROR);
				result.put("message", "参数有误");
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			result.put("code", XboxUtils.STATUS_ERROR);
			result.put("message", " 系统错误");
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}

	}
	
	/**
	 * 获取有订单来往的客户列表
	 * 
	 * @param response
	 * @param userphone
	 * @throws Exception
	 */
	@RequestMapping("/getKhList.do")
	public void getKhList(HttpServletResponse response,
			@RequestParam(value = "useridCj", required = false) String useridCj,
			@RequestParam(value = "cityName", required = false) String cityName) throws Exception {

		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();

		try {
			// 如果用户编码不为空
			if (StringUtils.isNotEmpty(useridCj)) {
				List<DdjCompany> companyList=companyService.getJgdKhList(Long.valueOf(useridCj));
				// 输出json对象
				JSONObject json = new JSONObject();
				JSONArray array = new JSONArray();
				if (companyList != null && companyList.size() > 0) {
					for (DdjCompany ddjCompany : companyList) {
						JSONObject jsonObject = new JSONObject();
						DdjUser queryUser1 = new DdjUser();
						queryUser1.setCompanyid(ddjCompany.getCompanyid());
						queryUser1.setUsertype((short) (1));
						queryUser1.setStatus((short) (1));
						List<DdjUser> cjUser = userService.getByValues(queryUser1);
						if (null != cjUser && cjUser.size() > 0) {
							long useridkh1 = cjUser.get(0).getUserid();
							String username = cjUser.get(0).getUsername();
							
							jsonObject.put("userid", useridkh1);
							jsonObject.put("username", username);
							jsonObject.put("companyid", ddjCompany.getCompanyid());
							jsonObject.put("companyname", ddjCompany.getCompanyname());
							jsonObject.put("logo", ddjCompany.getLogo() == null ? "" : ddjCompany.getLogo());
							jsonObject.put("address", ddjCompany.getAddress());
							jsonObject.put("phone", ddjCompany.getPhone());
							array.put(jsonObject);
						} else {
							// 说明没有找到对应的客户
						}
					}
				}
				json.put("myList", array);
				result.put("result", json);
			}
			else{
				result.put("code", XboxUtils.STATUS_ERROR);
				result.put("message", "参数有误");
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			result.put("code", XboxUtils.STATUS_ERROR);
			result.put("message", " 系统错误");
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}

	}
	

	/**
	 * 获取和客户有订单的所有厂家
	 * 
	 * @param response
	 * @param useridKh
	 * @throws JSONException
	 */
	@RequestMapping("/getZdVender.do")
	public void getZdVender(HttpServletResponse response,
			@RequestParam(value = "useridKh", required = false) String useridKh) throws JSONException {

		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();

		try {

			// 如果用户编码不为空
			if (StringUtils.isNotEmpty(useridKh)) {
				List<DdjCompany> ddjCompanys = companyService.getJgdCjList(Long.valueOf(useridKh));
				JSONObject json = new JSONObject();
				JSONArray array = new JSONArray();
				if (ddjCompanys != null && ddjCompanys.size() > 0) {
					for (DdjCompany ddjCompany : ddjCompanys) {
						JSONObject jsonObject = new JSONObject();
						DdjUser queryUser1 = new DdjUser();
						queryUser1.setCompanyid(ddjCompany.getCompanyid());
						queryUser1.setUsertype((short) (2));
						long useridCj = 0l;
						List<DdjUser> cjUser = userService.getByValues(queryUser1);
						if (null != cjUser && cjUser.size() > 0) {
							useridCj = cjUser.get(0).getUserid();
							jsonObject.put("companyid", ddjCompany.getCompanyid());
							jsonObject.put("useridCj", useridCj);
							jsonObject.put("name", ddjCompany.getCompanyname());
							jsonObject.put("logo", ddjCompany.getLogo() == null ? "" : ddjCompany.getLogo());
							jsonObject.put("address", ddjCompany.getAddress());
							jsonObject.put("phone", ddjCompany.getPhone());
							jsonObject.put("pfen", ddjCompany.getPfen() + "");
							array.put(jsonObject);
						} else {
							// 说明没有找到对应的厂家
						}
					}
				}
				json.put("myList", array);
				result.put("result", json);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			result.put("code", XboxUtils.STATUS_ERROR);
			result.put("message", " 系统错误");
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}
	}

	/**
	 * 获取厂家详情
	 * 
	 * @param response
	 * @param ddjUser
	 * @throws Exception
	 */
	@RequestMapping("/getVender/detail.do")
	public void getVenderDetail(HttpServletResponse response,
			@RequestParam(value = "useridCj", required = true) String useridCj,
			@RequestParam(value = "type", required = true) int type, DdjCompany ddjCompany) throws Exception {
		CommonResult commonResult = new CommonResult();

		try {

			if (StringUtils.isNotEmpty(useridCj)) {
				if (type == 1) {// 获取

					DdjUser ddjUser = userService.getById(useridCj);
					if (null != ddjUser) {
						long companyid = ddjUser.getCompanyid();
						DdjCompany company = companyService.getById(companyid + "");
						if (company != null) {
							commonResult.setResult(new JSONObject(company));
						} else {
							commonResult.setCode(XboxUtils.STATUS_ERROR);
							commonResult.setMessage("用户信息不存在");
						}

					} else {
						commonResult.setCode(XboxUtils.STATUS_ERROR);
						commonResult.setMessage("用户信息不存在");
					}

				} else {// 更新

					DdjUser ddjUser = userService.getById(useridCj);
					if (null != ddjUser) {
						long companyid = ddjUser.getCompanyid();
						ddjCompany.setCompanyid(companyid);
						companyService.updateValuesById(ddjCompany);
						;
					} else {
						commonResult.setCode(XboxUtils.STATUS_ERROR);
						commonResult.setMessage("用户信息不存在");
					}
				}
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("缺少参数");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(commonResult.getJsonObject());
			ResManager.close();
		}
	}

	/**
	 * 新增报价单
	 * 
	 * @param response
	 * @param ddjbjd
	 */
	@RequestMapping("/addBjd.do")
	public void addBjd(HttpServletResponse response, DdjBjd ddjbjd, String telephone) {
		CommonResult commonResult = new CommonResult();
		try {
			long khbh = 0l;
			DdjUser queryUser = new DdjUser();
			queryUser.setTelephone(telephone);
			queryUser.setUsertype((short) 1);
			List<DdjUser> userList = userService.getByValues(queryUser);
			if (null != userList && userList.size() > 0) {
				khbh = userList.get(0).getUserid();
				ddjbjd.setKhbh(khbh);
				ddjbjd.setStatus((short) 0);
				bjdService.save(ddjbjd);
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("没有查询到相关的客户信息");
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(new JSONObject(commonResult));
			ResManager.close();
		}
	}

	/**
	 * 确认报价单
	 * 
	 * @param response
	 * @param ywid
	 * @param status
	 */
	@RequestMapping("/qrbjd.do")
	public void qrbjd(HttpServletResponse response, String ywid, Short status) {
		CommonResult commonResult = new CommonResult();
		try {
			DdjBjd ddjBjd = bjdService.getById(ywid);
			if (ddjBjd != null) {
				ddjBjd.setStatus(status);
				bjdService.updateValuesById(ddjBjd);

				// 确认过后，查看合作列表，如果没有则添加
				long cjbh = ddjBjd.getCjbh();
				long khbh = ddjBjd.getKhbh();
				DdjKhCj ddjkhcj = new DdjKhCj();
				ddjkhcj.setUseridCj(cjbh);
				ddjkhcj.setUseridKh(khbh);
				List<DdjKhCj> ddjkhcjList = ddjkhcjService.getByValues(ddjkhcj);
				// 如果不存在合作关系，则增加合作关系
				if (null == ddjkhcjList || ddjkhcjList.isEmpty()) {
					ddjkhcjService.save(ddjkhcj);
				}

			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("没有查询到该报价单");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(new JSONObject(commonResult));
			ResManager.close();
		}
	}

	/**
	 * 
	 * 根据条件获取报价单
	 * 
	 * @param response
	 * @param page
	 * @param rows
	 * @param ddjbjd
	 */
	@RequestMapping("/getAllBjd.do")
	public void getAllBjd(HttpServletResponse response, int page, int rows,
			@RequestParam(value = "cjbh", required = false) String cjbh,
			@RequestParam(value = "khbh", required = false) String khbh,
			@RequestParam(value = "status", required = false) String status, DdjBjd ddjbjd) {
		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();
		try {

			PageRequest pageRequest = new PageRequest();
			pageRequest.setPageSize(rows);
			pageRequest.setPageNo(page);
			Criteria criteria = new Criteria();
			if (StringUtils.isNotEmpty(cjbh)) {
				criteria.addCriterion(new Criterion(" cjbh =" + cjbh + " "));
			}
			if (StringUtils.isNotEmpty(khbh)) {
				criteria.addCriterion(new Criterion(" khbh = " + khbh + " "));

			}
			if (StringUtils.isNotEmpty(status)) {
				criteria.addCriterion(new Criterion(" status = " + status + " "));

			}
			pageRequest.getCriteria().addAll(criteria.getAllCriteria());
			pageRequest.setOrderByClause(" order by bjtime desc ");
			Page<DdjBjd> ddjbjds = bjdService.findPage(pageRequest);

			// 输出json对象
			JSONObject json = new JSONObject();
			JSONArray array = new JSONArray();

			if (ddjbjds.getResult() != null && ddjbjds.getResult().size() > 0) {
				for (DdjBjd bjd : ddjbjds.getResult()) {

					JSONObject jsonObject = new JSONObject();
					jsonObject.put("ywid", bjd.getYwid());
					jsonObject.put("khbh", bjd.getKhbh());
					DdjUser khUser = userService.getById(bjd.getKhbh() + "");
					if (khUser != null) {
						Long companyid = khUser.getCompanyid();
						if (null != companyid) {
							DdjCompany ddjCompany = companyService.getById(companyid.longValue() + "");
							jsonObject.put("khcompanyname", null != ddjCompany ? ddjCompany.getCompanyname() : "");

						}

					}
					jsonObject.put("khname", null != khUser ? khUser.getUsername() : "");
					jsonObject.put("cjbh", bjd.getCjbh());
					DdjUser cjUser = userService.getById(bjd.getCjbh() + "");
					if (cjUser != null) {
						Long companyid = cjUser.getCompanyid();
						if (null != companyid) {
							DdjCompany ddjCompany = companyService.getById(companyid.longValue() + "");
							jsonObject.put("cjcompanyname", null != ddjCompany ? ddjCompany.getCompanyname() : "");
						}

					}
					jsonObject.put("cjname", null != cjUser ? cjUser.getUsername() : "");
					jsonObject.put("productname", bjd.getProductname());
					jsonObject.put("dzlx", bjd.getDzlx());
					jsonObject.put("cpcz", bjd.getCpcz());
					jsonObject.put("price", bjd.getPrice());
					array.put(jsonObject);
				}
			}
			json.put("list", array);
			result.put("result", json);
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}
	}

	/**
	 * 添加仓库
	 * 
	 * @param response
	 * @param ddjckb
	 */
	@RequestMapping("/addCk.do")
	public void addCk(HttpServletResponse response, DdjCkb ddjckb) {
		CommonResult commonResult = new CommonResult();
		try {

			ckbService.save(ddjckb);
			List<DdjCkb> ckbList = ckbService.getByValues(ddjckb);
			if (null != ckbList && !ckbList.isEmpty()) {
				commonResult.setResult(new JSONObject(ckbList.get(0)));
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setResult("没有找到对应的仓库记录");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(commonResult.getJsonObject());
			ResManager.close();
		}
	}

	/**
	 * 删除仓库 1、先删除仓库联系人 2、再删除仓库本身
	 * 
	 * @param response
	 * @param ckid
	 */
	@RequestMapping("/deleteCk.do")
	public void deleteCk(HttpServletResponse response, String ckid) {
		CommonResult commonResult = new CommonResult();
		try {
			DdjCkb ckb = ckbService.getById(ckid);
			if (null != ckb) {
				Long cklxrid = ckb.getCklxrid();
				if (null != cklxrid) {
					DdjUser user = userService.getById(cklxrid.longValue() + "");
					if (user != null) {
						// 把仓库联系人的状态标识为0
						user.setStatus((short) 0);
						userService.updateValuesById(user);
					}
				}
				// 根据仓库的状态
				ckb.setStatus((short) 0);
				ckbService.updateValuesById(ckb);
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("没有找到对应的仓库");
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(commonResult.getJsonObject());
			ResManager.close();
		}
	}

	/**
	 * 
	 * 更新仓库取货地址或者送货地址
	 * 
	 * @param response
	 * @param ckid
	 * @param cktype
	 * @param ckaddress
	 */
	@RequestMapping("/addCkDz.do")
	public void addCkDz(HttpServletResponse response, String ckid, String cktype, String ckaddress) {
		CommonResult commonResult = new CommonResult();
		try {
			DdjCkb ckb = ckbService.getById(ckid);
			if (cktype.equals("1")) {
				ckb.setCkqhdz(ckaddress);
			}
			if (cktype.equals("2")) {
				ckb.setCkshdz(ckaddress);
			}
			ckbService.saveOrUpdateById(ckb);
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(commonResult.getJsonObject());
			ResManager.close();
		}
	}

	/**
	 * 
	 * 更新仓库取货地址和送货地址
	 * 
	 * @param response
	 * @param ckid
	 * @param cktype
	 * @param ckaddress
	 */
	@RequestMapping("/updateCkDz.do")
	public void updateCkDz(HttpServletResponse response, String ckid, String qhaddress, String shaddress) {
		CommonResult commonResult = new CommonResult();
		try {
			DdjCkb ckb = ckbService.getById(ckid);
			ckb.setCkqhdz(qhaddress);
			ckb.setCkshdz(shaddress);
			ckbService.saveOrUpdateById(ckb);
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(commonResult.getJsonObject());
			ResManager.close();
		}
	}

	/**
	 * 增加仓库联系人
	 * 
	 * @param response
	 * @param ddjckb
	 */
	@RequestMapping("/addCkLxr.do")
	public void addCkLxr(HttpServletResponse response, DdjCkb ddjckb, String lxrname, String lxrphone) {
		CommonResult commonResult = new CommonResult();
		try {
			DdjCkb ckb = ckbService.getById("" + ddjckb.getCkid());
			if (null != ckb) {
				long userid = ckb.getUserid();
				DdjUser ddjuser = userService.getById(userid + "");
				long companyid = ddjuser.getCompanyid();
				int cktype = ckb.getCktype() == 1 ? 3 : 4;
				String zcnumber = ddjuser.getZcnumber();

				// 保存到用户表
				DdjUser user = new DdjUser();
				user.setTelephone(lxrphone);

				List<DdjUser> ddjUserList = userService.getByValues(user);
				if (null != ddjUserList && !ddjUserList.isEmpty()) {
					// 说明该仓库联系人已经存在
					DdjUser tempUser = ddjUserList.get(0);
					Short status = tempUser.getStatus();
					int usertype = tempUser.getUsertype();
					if (usertype == ckb.getCktype()) {
						if (null != status) {
							if (status == 1) {
								commonResult.setCode(XboxUtils.STATUS_ERROR);
								commonResult.setMessage("仓库联系人已经存在");
							} else {
								tempUser.setStatus((short) 1);
								tempUser.setPassword("888888");
								tempUser.setCompanyid(companyid);
								userService.updateValuesById(tempUser);
							}
						}
					} else {
						commonResult.setCode(XboxUtils.STATUS_ERROR);
						commonResult.setMessage("该手机号已经存在");
					}
				} else {
					user.setCompanyid(companyid);
					user.setUsertype(Short.valueOf(cktype + ""));
					user.setZcnumber(zcnumber);
					user.setUsername(lxrname);
					user.setPassword("888888");
					user.setStatus((short) 1);
					user.setAudit((short) 1);
					userService.save(user);
				}
				// 更新仓库表
				String cklxrid = "" + userService.getByValues(user).get(0).getUserid();
				ckb.setCklxrid(Long.valueOf(cklxrid));
				ckbService.updateValuesById(ckb);

			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("仓库表不存在");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(commonResult.getJsonObject());
			ResManager.close();
		}
	}

	/**
	 * 获取仓库地址
	 * 
	 * @param response
	 * @param userphone
	 * @param type
	 * @throws Exception
	 */
	@RequestMapping("/getAddress.do")
	public void getAddress(HttpServletResponse response, @RequestParam(value = "ckid", required = false) String ckid,
			@RequestParam(value = "type", required = false) String type) throws Exception {
		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();
		try {
			String address = "";
			DdjCkb ckb = ckbService.getById(ckid);
			if (null != ckb) {
				if (null != type) {
					if (type.equals("1")) {
						address = ckb.getCkqhdz();
					} else if (type.equals("2")) {
						address = ckb.getCkshdz();
					}
					result.put("address", address);
				} else {
					result.put("qhaddress", ckb.getCkqhdz());
					result.put("shaddress", ckb.getCkshdz());
				}
			}
			result.put("ckid", ckb.getCkid());
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}
	}

	/**
	 * 
	 * 获取仓库联系人
	 * 
	 * @param response
	 * @param ckid
	 * @throws Exception
	 */
	@RequestMapping("/getCkLxr.do")
	public void getCkLxr(HttpServletResponse response, @RequestParam(value = "ckid", required = false) String ckid)
			throws Exception {
		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();
		try {
			DdjCkb ckb = ckbService.getById(ckid);
			long userid = ckb.getCklxrid();
			DdjUser user = userService.getById(userid + "");
			if (user.getStatus() == 1) {
				result.put("userid", user.getUserid());
				result.put("username", user.getUsername());
				result.put("userphone", user.getTelephone());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}
	}

	/**
	 * 删除仓库联系人
	 * 
	 * @param response
	 * @param ckid
	 * @param lxrid
	 */
	@RequestMapping("/deleteCkLxr.do")
	public void deleteCkLxr(HttpServletResponse response, @RequestParam(value = "ckid", required = false) String ckid,
			@RequestParam(value = "lxrid", required = false) String lxrid) {
		CommonResult commonResult = new CommonResult();
		try {
			DdjCkb ckb = ckbService.getById(ckid);
			if (null != ckb) {
				Long cklxrid = ckb.getCklxrid();
				if (null != cklxrid) {
					DdjUser user = userService.getById("" + cklxrid.longValue());
					user.setStatus((short) 0);
					userService.updateValuesById(user);
				}
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("没有找到对应的仓库");
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(commonResult.getJsonObject());
			ResManager.close();
		}
	}

	/**
	 * 生成订单
	 * 
	 * @param response
	 * @param ddjjgd
	 */
	@RequestMapping("/order/commit.do")
	public void orderCommit(HttpServletResponse response, HttpServletRequest request) {
		CommonResult commonResult = new CommonResult();
		try {

			String ywid = request.getParameter("ywid");
			String khqhdz = request.getParameter("khqhdz");
			String khshdz = request.getParameter("khshdz");
			String quantitytype = request.getParameter("quantitytype");
			String quantity = request.getParameter("quantity");
			String photos = request.getParameter("photos");

			if (null == ywid || ywid.equals("")) {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("没有传入报价单号");
			} else {
				DdjBjd bjd = bjdService.getById(ywid);
				if (bjd != null) {
					long khbh = bjd.getKhbh();
					long cjbh = bjd.getCjbh();

					String productName = bjd.getProductname();

					String cpcz = bjd.getCpcz();

					String dzlx = bjd.getDzlx();

					double price = bjd.getPrice();

					DdjJgd ddjjgd = new DdjJgd();
					ddjjgd.setKhid(khbh);
					ddjjgd.setCjid(cjbh);
					ddjjgd.setProductname(productName);
					ddjjgd.setCpcz(cpcz);
					ddjjgd.setDzlx(dzlx);
					ddjjgd.setPrice(price);
					ddjjgd.setKhqhdz(khqhdz);
					ddjjgd.setKhshdz(khshdz);
					ddjjgd.setQuantity(quantity);
					ddjjgd.setQuantitytype(
							quantitytype != null && !"".equals(quantitytype) ? Integer.parseInt(quantitytype) : 1);
					double totalprice = Arith
							.mul(((null != quantity && !quantity.equals("")) ? Integer.parseInt(quantity) : 0), price);
					ddjjgd.setTotalprice(totalprice);
					ddjjgd.setPhotos(photos);
					ddjjgd.setStatus(1);
					jgdService.saveOrUpdateById(ddjjgd);

				} else {
					commonResult.setCode(XboxUtils.STATUS_ERROR);
					commonResult.setMessage("没有查询到相应的报价单");
				}

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(commonResult.getJsonObject());
			ResManager.close();
		}
	}

	/*
	 * 获取订单列表
	 * 
	 * @param response
	 * 
	 * @param request
	 */
	@RequestMapping("/order/list.do")
	public void getOrderList(HttpServletResponse response, HttpServletRequest request, int page, int rows) {
		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();
		try {

			// 订单状态
			String status = request.getParameter("status");
			// 用户编码（客户、厂家、厂家仓库联系人、客户仓库联系人、司机）
			String userid = request.getParameter("userid");
			// 如果用户编码不为空
			if (null != userid && !userid.equals("")) {

				StringBuffer sbf = new StringBuffer();
				// 获取登录的用户信息
				DdjUser user = userService.getById(userid);
				// 用户类型
				int usertype = user.getUsertype();
				// 公司编码
				long companyid = user.getCompanyid();

				PageRequest pageRequest = new PageRequest();
				pageRequest.setPageSize(rows);
				pageRequest.setPageNo(page);
				Criteria criteria = new Criteria();

				logger.debug("用户类型=" + usertype);

				// 客户
				if (usertype == 1) {
					criteria.addCriterion(new Criterion("  khid = " + userid + " "));
				}
				// 客户仓库
				else if (usertype == 4) {
					DdjCkb ckb = new DdjCkb();
					ckb.setCklxrid(Long.valueOf(userid));
					List<DdjCkb> ckbList = ckbService.getByValues(ckb);
					if (null != ckbList && ckbList.size() > 0) {
						long lbUserid = ckbList.get(0).getUserid();
						criteria.addCriterion(new Criterion("  khid = " + lbUserid + " "));
					} else {
						criteria.addCriterion(new Criterion("  khid = -1 "));
					}
				}
				// 厂家
				else if (usertype == 2) {
					criteria.addCriterion(new Criterion("  cjid = " + userid + " "));
				}
				// 厂家仓库
				else if (usertype == 3) {
					DdjCkb ckb = new DdjCkb();
					ckb.setCklxrid(Long.valueOf(userid));
					List<DdjCkb> ckbList = ckbService.getByValues(ckb);
					if (null != ckbList && ckbList.size() > 0) {
						long lbUserid = ckbList.get(0).getUserid();
						logger.debug("用户编码=" + lbUserid);
						criteria.addCriterion(new Criterion("  cjid = " + lbUserid + " "));
					} else {
						// 如果查询不到联系人所在的仓库
						criteria.addCriterion(new Criterion("  cjid = -1 "));
					}
				}

				// 司机
				else if (usertype == 5) {
					DdjUser user_temp = new DdjUser();
					user_temp.setCompanyid(companyid);
					user_temp.setUsertype((short) 2);

					List<DdjUser> usercjList = userService.getByValues(user_temp);

					// 一个司机只有一个厂家
					if (null != usercjList && !usercjList.isEmpty()) {
						long lbUserid = usercjList.get(0).getUserid();
						criteria.addCriterion(new Criterion("  cjid = " + lbUserid + " "));
					}

					DdjSjKhck sjkhck = new DdjSjKhck();
					sjkhck.setSjbh(Long.valueOf(userid));
					// 获得该司机的关联的客户仓库
					List<DdjSjKhck> sjkhckList = sjkhckService.getByValues(sjkhck);
					for (int i = 0; i < sjkhckList.size(); i++) {
						DdjSjKhck tempSjKhck = sjkhckList.get(i);
						long ckbh = tempSjKhck.getCkbh();
						DdjCkb tempckb = ckbService.getById(ckbh + "");
						// 用户编号
						long user_id = tempckb.getUserid();
						if (i != sjkhckList.size() - 1) {
							sbf.append(user_id + ",");
						} else {
							sbf.append(user_id);
						}
					}
					if (StringUtils.isNotEmpty(sbf.toString())) {
						criteria.addCriterion(new Criterion("  khid in (" + sbf.toString() + ")  "));
					} else {
						criteria.addCriterion(new Criterion("  khid =-1  "));
					}
				}

				if (StringUtils.isNotEmpty(status)) {
					// 正在进行中的
					if (status.equals("1")) {
						criteria.addCriterion(new Criterion("  status <> 7 "));
					}
					// 已经结束的
					if (status.equals("2")) {
						criteria.addCriterion(new Criterion("  status = 7 "));
					}

				}
				pageRequest.getCriteria().addAll(criteria.getAllCriteria());
				// 输出json对象
				JSONObject json = new JSONObject();
				JSONArray array = new JSONArray();

				Page<DdjJgd> ddjjgds = jgdService.findPage(pageRequest);

				if (ddjjgds.getResult() != null && ddjjgds.getResult().size() > 0) {
					for (DdjJgd jgd : ddjjgds.getResult()) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("gdid", jgd.getGdid());
						jsonObject.put("cssj", DateUtils.getLongDateStr(jgd.getCssj()));
						jsonObject.put("khid", jgd.getKhid());
						DdjUser khuser = userService.getById(jgd.getKhid() + "");
						if (khuser != null) {
							Long companyid_ = khuser.getCompanyid();
							if (null != companyid_) {
								DdjCompany ddjCompany = companyService.getById(companyid_.longValue() + "");
								jsonObject.put("khcompanyname", null != ddjCompany ? ddjCompany.getCompanyname() : "");
							}

						}
						jsonObject.put("khname", null != khuser ? khuser.getUsername() : "");
						jsonObject.put("cjid", jgd.getCjid());
						DdjUser cjuser = userService.getById(jgd.getCjid() + "");
						if (cjuser != null) {
							Long companyid_ = cjuser.getCompanyid();
							if (null != companyid_) {
								DdjCompany ddjCompany = companyService.getById(companyid_.longValue() + "");
								jsonObject.put("cjcompanyname", null != ddjCompany ? ddjCompany.getCompanyname() : "");
							}

						}
						jsonObject.put("cjname", null != cjuser ? cjuser.getUsername() : "");
						jsonObject.put("productname", jgd.getProductname());
						jsonObject.put("dzlx", jgd.getDzlx());
						jsonObject.put("cpcz", jgd.getCpcz());
						jsonObject.put("price", jgd.getPrice());
						jsonObject.put("status", jgd.getStatus());
						jsonObject.put("quantity", jgd.getQuantity());
						jsonObject.put("quantitytype", jgd.getQuantitytype());
						DdjUser khUser = userService.getById("" + jgd.getKhid());
						if (null != khUser) {
							jsonObject.put("khphone", khUser.getTelephone());
							// 客户仓库管理员
							DdjUser queryUser = new DdjUser();
							queryUser.setUsertype((short) 4);
							queryUser.setStatus((short) 1);
							queryUser.setCompanyid(khUser.getCompanyid());
							List<DdjUser> khcklxr = userService.getByValues(queryUser);
							if (null != khcklxr && khcklxr.size() > 0) {
								DdjUser khckUser = khcklxr.get(0);
								if (null != khckUser) {
									jsonObject.put("khckphone", khckUser.getTelephone());
								}
							}
						}

						DdjUser cjUser = userService.getById("" + jgd.getCjid());
						if (null != cjUser) {
							jsonObject.put("cjphone", cjUser.getTelephone());

							// 厂家仓库管理员
							DdjUser queryUser = new DdjUser();
							queryUser.setUsertype((short) 3);
							queryUser.setStatus((short) 1);
							queryUser.setCompanyid(cjUser.getCompanyid());
							List<DdjUser> cjcklxr = userService.getByValues(queryUser);
							if (null != cjcklxr && cjcklxr.size() > 0) {
								DdjUser cjckUser = cjcklxr.get(0);
								if (null != cjckUser) {
									jsonObject.put("cjckphone", cjckUser.getTelephone());
								}

							}
						}

						// 查询该工单对应的司机号码
						DdjCkb kh_ckb = new DdjCkb();
						kh_ckb.setUserid(jgd.getKhid());
						List<DdjCkb> ckbList_sj = ckbService.getByValues(kh_ckb);
						if (null != ckbList_sj && ckbList_sj.size() > 0) {
							DdjCkb sj_ckb = ckbList_sj.get(0);
							if (null != sj_ckb) {
								DdjSjKhck sjkhck = new DdjSjKhck();
								sjkhck.setCkbh(sj_ckb.getCkid());
								List<DdjSjKhck> sjkhckList = sjkhckService.getByValues(sjkhck);
								
								if (null != sjkhckList && sjkhckList.size() > 0) {
									for(int i=0;i<sjkhckList.size();i++){
										
										long sjbh = sjkhckList.get(i).getSjbh();
										DdjUser sjUser = userService.getById("" + sjbh);
										if(sjUser.getStatus().intValue()==1){
											if(sjUser.getCompanyid().longValue()==cjUser.getCompanyid().longValue()){
												jsonObject.put("sjphone", sjUser.getTelephone());
												break;
											}
											else{
												//继续找
											}
										}
									}
								}
								
							}
						}
						// jsonObject.put("price", jgd.getPrice());
						array.put(jsonObject);
					}
				}
				json.put("list", array);
				result.put("result", json);
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("没有找到用户编码");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}
	}

	/**
	 * 获得工单的详细信息
	 * 
	 * @param response
	 * @param gdid
	 */
	@RequestMapping("/order/detail.do")
	public void getOrderDetail(HttpServletResponse response, String gdid) {
		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();
		try {

			DdjJgd jgd = jgdService.getById(gdid);
			if (null != jgd) {
				result.put("gdid", jgd.getGdid());
				result.put("cssj", DateUtils.getDateStr(jgd.getCssj()));
				result.put("khid", jgd.getKhid());
				DdjUser khuser = userService.getById(jgd.getKhid() + "");
				if (khuser != null) {
					Long companyid_ = khuser.getCompanyid();
					if (null != companyid_) {
						DdjCompany ddjCompany = companyService.getById(companyid_.longValue() + "");
						result.put("khcompanyname", null != ddjCompany ? ddjCompany.getCompanyname() : "");
					}

				}
				result.put("khname", null != khuser ? khuser.getUsername() : "");
				result.put("cjid", jgd.getCjid());
				DdjUser cjuser = userService.getById(jgd.getCjid() + "");
				if (cjuser != null) {
					Long companyid_ = cjuser.getCompanyid();
					if (null != companyid_) {
						DdjCompany ddjCompany = companyService.getById(companyid_.longValue() + "");
						result.put("cjcompanyname", null != ddjCompany ? ddjCompany.getCompanyname() : "");
					}

				}
				result.put("cjname", null != cjuser ? cjuser.getUsername() : "");
				result.put("productname", jgd.getProductname());
				result.put("dzlx", jgd.getDzlx());
				result.put("cpcz", jgd.getCpcz());
				result.put("price", jgd.getPrice());
				result.put("photos", jgd.getPhotos());
				result.put("status", jgd.getStatus());
				result.put("khqhdz", jgd.getKhqhdz());
				result.put("khshdz", jgd.getKhshdz());
				result.put("quantitytype", jgd.getQuantitytype());
				result.put("quantity", jgd.getQuantity());
				result.put("totalPrice", jgd.getTotalprice());
				DdjUser khUser = userService.getById("" + jgd.getKhid());
				if (null != khUser) {
					result.put("khphone", khUser.getTelephone());
					// 客户仓库管理员
					DdjUser queryUser = new DdjUser();
					queryUser.setUsertype((short) 4);
					queryUser.setCompanyid(khUser.getCompanyid());
					queryUser.setStatus((short) 1);
					List<DdjUser> khcklxr = userService.getByValues(queryUser);
					if (null != khcklxr && khcklxr.size() > 0) {
						DdjUser khckUser = khcklxr.get(0);
						if (null != khckUser) {
							result.put("khckphone", khckUser.getTelephone());
						}
					}
				}

				DdjUser cjUser = userService.getById("" + jgd.getCjid());
				if (null != cjUser) {
					result.put("cjphone", cjUser.getTelephone());

					// 厂家仓库管理员
					DdjUser queryUser = new DdjUser();
					queryUser.setUsertype((short) 3);
					queryUser.setStatus((short) 1);
					queryUser.setCompanyid(cjUser.getCompanyid());
					List<DdjUser> cjcklxr = userService.getByValues(queryUser);
					if (null != cjcklxr && cjcklxr.size() > 0) {
						DdjUser cjckUser = cjcklxr.get(0);
						if (null != cjckUser) {
							result.put("cjckphone", cjckUser.getTelephone());
						}

					}
				}

				// 查询该工单对应的司机号码
				DdjCkb kh_ckb = new DdjCkb();
				kh_ckb.setUserid(jgd.getKhid());
				List<DdjCkb> ckbList_sj = ckbService.getByValues(kh_ckb);
				if (null != ckbList_sj && ckbList_sj.size() > 0) {
					DdjCkb sj_ckb = ckbList_sj.get(0);
					if (null != sj_ckb) {
						DdjSjKhck sjkhck = new DdjSjKhck();
						sjkhck.setCkbh(sj_ckb.getCkid());

						List<DdjSjKhck> sjkhckList = sjkhckService.getByValues(sjkhck);
						for(int i=0;i<sjkhckList.size();i++){
							long sjbh = sjkhckList.get(i).getSjbh();
							DdjUser sjUser = userService.getById("" + sjbh);
							if(sjUser.getCompanyid().longValue()==cjUser.getCompanyid().longValue()){
								result.put("sjphone", sjUser.getTelephone());
								break;
							}
							else{
								//继续找
							}
						}
					}
				}

			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("未找到该加工单");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}
	}

	/**
	 * 更新工单状态
	 * 
	 * @param response
	 * @param gdid
	 * @param status
	 * @throws Exception
	 */
	@RequestMapping("/order/update.do")
	public void orderUpdate(HttpServletResponse response, @RequestParam(value = "gdid", required = true) String gdid,
			int status) throws Exception {
		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();
		try {
			DdjJgd ddjJgd = jgdService.getById(gdid);

			if (ddjJgd != null) {
				ddjJgd.setStatus(status);
				jgdService.updateValuesById(ddjJgd);
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("没有找到相应的工单");
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}
	}

	/**
	 * 增加订单的进出货详情 1、等待取货——客户仓库提交加工单状态变成“等待取货”
	 * 2、已取产品——司机前往客户仓库取货，并点击公斤数且确认后，状态变为“已取产品” 3、产品加工——厂家仓库点击公斤数并确认后，状态变为”产品加工”
	 * 4、产品出库（部分)——厂家仓库点击部分出库后，状态即变为“产品出库（部分）”
	 * 5、产品出库（全部）——厂家仓库点击全部出库后，状态即变为“产品出库（全部）”
	 * 6、客户收货（部分）——客户仓库点击部分收货后，状态即变为“客户收货（部分）
	 * 7、客户收货（全部）——客户仓库点击全部收货后，状态即变为“客户收货（全部）
	 * 
	 * @param response
	 * @param ddjjchxq
	 */
	@RequestMapping("/order/addDetail.do")
	public void addDetail(HttpServletResponse response, DdjJgdJchxq ddjjchxq, String allsh) {
		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();
		try {

			String gdid = ddjjchxq.getGdid();
			DdjJgd jgd_src = jgdService.getById(gdid);
			int status = jgd_src.getStatus();
			// String cpnum_src = jgd_src.getQuantity();
			double cpnum = ddjjchxq.getQuantity();
			DdjJgd jgd = new DdjJgd();
			jgd.setGdid(gdid);
			String detail = ddjjchxq.getJchdetail();
			if (detail.equals("1")) {
				// 已取产品
				jgd.setStatus(2);
				jgdService.updateValuesById(jgd);
			} else if (detail.equals("2")) {
				// 产品加工
				jgd.setStatus(3);
				jgdService.updateValuesById(jgd);
			}
			// 部分出库
			else if (detail.equals("3")) {
				// 第一次加工完出厂
				if (status < 4) {
					// 加工完出库中
					jgd.setStatus(4);
					jgdService.updateValuesById(jgd);
				}
			}
			// 全部出库
			else if (detail.equals("4")) {
				jgd.setStatus(5);
				jgdService.updateValuesById(jgd);
			}
			// 部分收货
			else if (detail.equals("5")) {
				/*
				 * String jchdetail1=""; DdjJgdJchxq ddjjchxq1 = new
				 * DdjJgdJchxq(); ddjjchxq1.setGdid(gdid);
				 * ddjjchxq1.setUsertype((short)3); List<DdjJgdJchxq>
				 * ddjjchxqList1 = jgdjchxqService.getByValues(ddjjchxq1);
				 * if(null!=ddjjchxqList1&&!ddjjchxqList1.isEmpty()){
				 * jchdetail1=ddjjchxqList1.get(0).getJchdetail(); } //部分收获
				 * if(jchdetail1.equals("3")){ jgd.setStatus(6);
				 * jgdService.updateValuesById(jgd); } //全部收货
				 * if(jchdetail1.equals("4")){ jgd.setStatus(7); DdjJgdJchxq
				 * jchxq = new DdjJgdJchxq(); jchxq.setGdid(gdid);
				 * jchxq.setUsertype((short)4); List<DdjJgdJchxq> jchxqList =
				 * jgdjchxqService.getByValues(jchxq); double total_num=0;
				 * if(null!=jchxqList&&!jchxqList.isEmpty()){ for(DdjJgdJchxq
				 * jchxqVO:jchxqList){ total_num=
				 * total_num+jchxqVO.getQuantity(); } total_num=total_num+cpnum;
				 * } jgd.setTotalprice(Arith.mul(jgd.getPrice(), total_num));
				 * jgdService.updateValuesById(jgd); }
				 */
			} else if (detail.equals("6")) {
				jgd.setStatus(6);
				jgdService.updateValuesById(jgd);
			} else if (detail.equals("7")) {
				jgd.setStatus(7);
				DdjJgdJchxq jchxq = new DdjJgdJchxq();
				jchxq.setGdid(gdid);
				jchxq.setUsertype((short) 4);
				List<DdjJgdJchxq> jchxqList = jgdjchxqService.getByValues(jchxq);
				double total_num = 0;
				if (null != jchxqList && !jchxqList.isEmpty()) {
					for (DdjJgdJchxq jchxqVO : jchxqList) {
						total_num = total_num + jchxqVO.getQuantity();
					}
					total_num = total_num + cpnum;
				}
				jgd.setTotalprice(Arith.mul(jgd_src.getPrice(), total_num));
				jgdService.updateValuesById(jgd);
			}
			jgdjchxqService.save(ddjjchxq);

		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}
	}

	/**
	 * 获得订单的进出货详情
	 * 
	 * @param response
	 * @param ddjjchxq
	 */
	@RequestMapping("/order/getDetail.do")
	public void getDetail(HttpServletResponse response, String gdid, String usertype) {
		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();
		try {
			JSONObject json = new JSONObject();
			Map<String, String> map = XboxUtils.getDetailMsg();
			if (StringUtils.isNotEmpty(gdid)) {
				DdjJgd ddjJgd = jgdService.getById(gdid);
				int status = ddjJgd.getStatus();
				DdjJgdJchxq ddjjchxq = new DdjJgdJchxq();
				ddjjchxq.setGdid(gdid);
				String jchdetail = "";
				List<DdjJgdJchxq> ddjjchxqList = jgdjchxqService.getByValues(ddjjchxq);
				if (null != ddjjchxqList && !ddjjchxqList.isEmpty()) {
					jchdetail = ddjjchxqList.get(0).getJchdetail();
				}
				if (StringUtils.isNotEmpty(usertype)) {
					// 获得该角色的下一个动作
					Map<String, String> retMap = this.getNextAction(usertype, status, jchdetail, gdid);
					if (null != retMap && !retMap.isEmpty()) {
						json.put("jchflag", retMap.get("jchflag"));
						json.put("jchflagname", retMap.get("jchflagname"));
					}

					// 厂家仓库、客户仓库、司机只能查看自己的
					if (usertype.equals("3") || usertype.equals("4") || usertype.equals("5")) {
						ddjjchxq.setUsertype(Short.valueOf(usertype));
					}
				}
				List<DdjJgdJchxq> ddjjchxqs = jgdjchxqService.getByValues(ddjjchxq);
				if (null != ddjjchxqs && ddjjchxqs.size() > 0) {
					// 输出json对象

					JSONArray array = new JSONArray();
					for (DdjJgdJchxq jchxq : ddjjchxqs) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("gdid", jchxq.getGdid());
						jsonObject.put("qrtime", DateUtils.getLongDateStr(jchxq.getQrtime()));
						jsonObject.put("jchdetail", map.get(jchxq.getJchdetail()));
						jsonObject.put("quantity", jchxq.getQuantity());
						jsonObject.put("userid", jchxq.getUserid());
						jsonObject.put("usertype", jchxq.getUsertype());
						array.put(jsonObject);
					}
					json.put("list", array);

				} else {
					commonResult.setCode(XboxUtils.STATUS_ERROR);
					commonResult.setMessage("未找到该加工单的进出货详情");
				}
				result.put("result", json);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}
	}

	/**
	 * 获得下一个动作
	 * 
	 * @param usertype
	 *            用户类型
	 * @param status
	 *            订单的当前状态
	 * @param jchdetail
	 *            最新的一次详情
	 * @param gdid
	 *            工单编号
	 * @return
	 */
	private Map<String, String> getNextAction(String usertype, int status, String jchdetail, String gdid) {
		Map<String, String> retMap = new HashMap<String, String>();
		// 厂家仓库
		if (usertype.equals("3")) {
			// 等待取货
			if (status == 1) {

			}
			// 已取产品
			else if (status == 2) {
				if (jchdetail.equals("1")) {
					retMap.put("jchflag", "2");
					retMap.put("jchflagname", "收到司机未加工产品");
				} else {

				}
			}
			// 产品加工
			else if (status == 3) {
				if (jchdetail.equals("2")) {
					retMap.put("jchflag", "3|4");
					retMap.put("jchflagname", "交由司机部分加工完成的产品|交由司机全部加工完成的产品");
				}
			}
			// 加工完部分出厂
			else if (status == 4) {

			}
			// 加工完全部出厂
			else if (status == 5) {

			}
			// 客户部分收货
			else if (status == 6) {
				if (jchdetail.equals("6")) {
					retMap.put("jchflag", "3|4");
					retMap.put("jchflagname", "交由司机部分加工完成的货物|交由司机全部加工完成的货物");
				}

			}
			// 客户全部出货
			else if (status == 7) {

			}
		}
		// 客户仓库
		else if (usertype.equals("4")) {
			// 等待取货
			if (status == 1) {

			}
			// 已出货
			else if (status == 2) {

			}
			// 进厂加工
			else if (status == 3) {

			}
			// 加工完部分出厂
			else if (status == 4) {
				if (jchdetail.equals("5")) {
					retMap.put("jchflag", "6");
					retMap.put("jchflagname", "部分收到加工完成的产品");
				}
			}
			// 加工完全部出厂
			else if (status == 5) {
				if (jchdetail.equals("5")) {
					retMap.put("jchflag", "7");
					retMap.put("jchflagname", "全部收到加工完成的产品");
				}
			}
			// 客户部分收货
			else if (status == 6) {
				if (jchdetail.equals("5")) {
					String jchdetail1 = "";
					DdjJgdJchxq ddjjchxq1 = new DdjJgdJchxq();
					ddjjchxq1.setGdid(gdid);
					ddjjchxq1.setUsertype((short) 3);
					List<DdjJgdJchxq> ddjjchxqList1 = jgdjchxqService.getByValues(ddjjchxq1);
					if (null != ddjjchxqList1 && !ddjjchxqList1.isEmpty()) {
						jchdetail1 = ddjjchxqList1.get(0).getJchdetail();
					}
					if (jchdetail1.equals("3")) {
						retMap.put("jchflag", "6");
						retMap.put("jchflagname", "部分收到加工完成的产品");

					}
					if (jchdetail1.equals("4")) {
						retMap.put("jchflag", "7");
						retMap.put("jchflagname", "全部收到加工完成的产品");
					}

				}

			}
			// 客户全部出货
			else if (status == 7) {

			}
		}
		// 司机
		else if (usertype.equals("5")) {
			// 等待取货
			if (status == 1) {
				if (jchdetail.equals("")) {
					retMap.put("jchflag", "1");
					retMap.put("jchflagname", "已从客户仓库拉回未加工的产品");
				} else {

				}
			}
			// 已取产品
			else if (status == 2) {

			}
			// 产品加工
			else if (status == 3) {

			}
			// 加工完部分出厂
			else if (status == 4) {
				if (jchdetail.equals("3")) {
					retMap.put("jchflag", "5");
					retMap.put("jchflagname", "将完成的产品送往仓库");
				}
			}
			// 加工完全部出厂
			else if (status == 5) {

				if (jchdetail.equals("4")) {
					retMap.put("jchflag", "5");
					retMap.put("jchflagname", "将完成的产品送往仓库");
				}
			}
			// 客户部分收货
			else if (status == 6) {
				if (jchdetail.equals("3")) {
					retMap.put("jchflag", "5");
					retMap.put("jchflagname", "将完成的产品送往仓库");
				}
				if (jchdetail.equals("4")) {
					retMap.put("jchflag", "5");
					retMap.put("jchflagname", "将完成的产品送往仓库");
				}
			}
		}
		return retMap;
	}

	/**
	 * 增加司机
	 * 
	 * @param response
	 */
	@RequestMapping("/driver/addDriver.do")
	public void addDriver(HttpServletResponse response, HttpServletRequest request) {
		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();
		try {
			String userid = request.getParameter("userid");
			DdjUser user = userService.getById("" + userid);
			long companyid = user.getCompanyid();
			DdjUser newUser = new DdjUser();
			newUser.setTelephone(request.getParameter("telephone"));
			List<DdjUser> myuserList = userService.getByValues(newUser);
			if (null != myuserList && myuserList.size() > 0) {
				DdjUser tempUser = myuserList.get(0);
				Short status = tempUser.getStatus();
				Short usertype = tempUser.getUsertype();
				if (usertype.shortValue() == 5) {
					if (status.shortValue() == 1) {
						commonResult.setCode(XboxUtils.STATUS_ERROR);
						commonResult.setMessage("该手机号已经存在");
					}
					if (status.shortValue() == 0) {
						tempUser.setStatus((short) 1);
						newUser.setPassword("888888");
						newUser.setCompanyid(companyid);
						userService.updateValuesById(tempUser);
					}
				}
				// 其他角色的用户已经用了这个手机号
				else {
					commonResult.setCode(XboxUtils.STATUS_ERROR);
					commonResult.setMessage("该手机号已经存在");
				}
			} else {
				newUser.setCompanyid(companyid);
				newUser.setLicence(request.getParameter("licence"));
				newUser.setUsername(request.getParameter("username"));
				newUser.setStatus((short) 1);
				newUser.setUsertype((short) 5);
				newUser.setPassword("888888");
				newUser.setStatus((short) 1);
				newUser.setAudit((short) 1);
				userService.save(newUser);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}
	}

	/**
	 * 删除司机
	 * 
	 * @param response
	 * @param userid
	 */
	@RequestMapping("/driver/delDriver.do")
	public void delDriver(HttpServletResponse response, String userid) {
		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();
		try {
			// 先删除关联的仓库
			DdjSjKhck sjkhck = new DdjSjKhck();
			sjkhck.setSjbh(Long.valueOf(userid));
			List<DdjSjKhck> sjkhckList = sjkhckService.getByValues(sjkhck);
			for (int i = 0; i < sjkhckList.size(); i++) {
				// sjkhckService.deleteById(sjkhckList.get(i));
			}
			// 把状态改变
			DdjUser user = userService.getById(userid + "");
			if (null != user) {
				user.setStatus((short) 0);
				userService.updateValuesById(user);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}
	}

	/**
	 * 删除司机关联的客户仓库
	 * 
	 * @param response
	 * @param userid
	 */
	@RequestMapping("/driver/delDriverGlCk.do")
	public void delDriverGlCk(HttpServletResponse response, long sjbh, long ckid) {
		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();
		try {
			// 先删除关联的仓库
			DdjSjKhck sjkhck = new DdjSjKhck();
			sjkhck.setSjbh(sjbh);
			sjkhck.setCkbh(ckid);
			sjkhckService.deleteById(sjkhck);
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}
	}

	/**
	 * 设置司机和客户仓库关联
	 * 
	 * @param response
	 * @param sjbh
	 * @param ckbh
	 */
	@RequestMapping("/driver/setDriverGlCk.do")
	public void setDriverGlCk(HttpServletResponse response, long sjbh, String telephone) {
		CommonResult commonResult = new CommonResult();
		try {
			DdjSjKhck ddjsjkhck = new DdjSjKhck();

			DdjUser ddjUser = new DdjUser();
			ddjUser.setTelephone(telephone);
			List<DdjUser> ddjUserList = userService.getByValues(ddjUser);
			if (null != ddjUserList && ddjUserList.size() > 0) {
				DdjUser user = ddjUserList.get(0);
				long cklxrid = user.getUserid();
				DdjCkb ckb = new DdjCkb();
				ckb.setCklxrid(cklxrid);
				List<DdjCkb> ckbList = ckbService.getByValues(ckb);
				logger.error("根据仓库联系人获得仓库列表=" + ckbList.size());
				if (null != ckbList && ckbList.size() > 0) {

					long ckid = ckbList.get(0).getCkid();
					ddjsjkhck.setCkbh(ckid);
					List<DdjSjKhck> sjkhckList = sjkhckService.getByValues(ddjsjkhck);
					if (null != sjkhckList && sjkhckList.size() > 0) {
						DdjUser usernow = userService.getById("" + sjbh);
						boolean isExist = false;
						for (int j = 0; j < sjkhckList.size(); j++) {
							DdjSjKhck sjkhckVO = sjkhckList.get(j);
							long sjbhid = sjkhckVO.getSjbh();
							DdjUser usertemp = userService.getById("" + sjbhid);
							if (usertemp.getCompanyid().longValue() == usernow.getCompanyid().longValue()) {
								isExist = true;
								break;
							}
						}
						if (isExist) {
							commonResult.setCode(XboxUtils.STATUS_ERROR);
							commonResult.setMessage("该客户仓库已经和商家某位司机相关联");
						} else {
							ddjsjkhck.setSjbh(sjbh);
							sjkhckService.save(ddjsjkhck);
						}

					} else {
						ddjsjkhck.setSjbh(sjbh);
						sjkhckService.save(ddjsjkhck);
					}
				} else {
					commonResult.setCode(XboxUtils.STATUS_ERROR);
					commonResult.setMessage("没有找到该仓库联系人");
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(new JSONObject(commonResult));
			ResManager.close();
		}
	}

	/**
	 * 获取司机的列表
	 * 
	 * @param response
	 * @param ckid
	 * @param page
	 * @param rows
	 */
	@RequestMapping("/driver/getDrivers.do")
	public void getDrivers(HttpServletResponse response, String ckid, int page, int rows) {
		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();
		try {
			DdjCkb ckb = ckbService.getById(ckid);
			long userid = ckb.getUserid();
			DdjUser user = userService.getById(userid + "");
			long companyid = user.getCompanyid();

			DdjUser queryUser = new DdjUser();
			queryUser.setCompanyid(companyid);
			queryUser.setUsertype((short) 5);
			queryUser.setStatus((short) 1);
			List<DdjUser> userList = userService.getByValues(queryUser);

			if (null != userList && userList.size() > 0) {
				// 输出json对象
				JSONObject json = new JSONObject();
				JSONArray array = new JSONArray();
				for (DdjUser ddjUser : userList) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("userid", ddjUser.getUserid());
					jsonObject.put("username", ddjUser.getUsername());
					jsonObject.put("telephone", ddjUser.getTelephone());
					jsonObject.put("licence", ddjUser.getLicence());

					// 获取该司机关联的客户仓库
					JSONArray array1 = new JSONArray();
					DdjSjKhck ddjsjkhck = new DdjSjKhck();
					ddjsjkhck.setSjbh(ddjUser.getUserid());
					List<DdjSjKhck> sjkhckList = sjkhckService.getByValues(ddjsjkhck);
					for (DdjSjKhck sjkhck : sjkhckList) {
						JSONObject jsonObject1 = new JSONObject();
						jsonObject1.put("ckid", sjkhck.getCkbh());
						DdjCkb ddjCkb = ckbService.getById("" + sjkhck.getCkbh());
						DdjUser ckUser = userService.getById(ddjCkb.getCklxrid() + "");
						jsonObject1.put("username", ckUser.getUsername());
						jsonObject1.put("telephone", ckUser.getTelephone());
						jsonObject1.put("address", ddjCkb.getCkaddress());
						array1.put(jsonObject1);
					}
					jsonObject.put("list", array1);
					array.put(jsonObject);
				}
				json.put("list", array);
				result.put("result", json);
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("未找到该加工单的进出货详情");
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}
	}

	/**
	 * 
	 * 
	 * 查询客户这个月和某个商家来往的账单列表
	 * @param response
	 * @param userid
	 * @param type
	 * @param year
	 * @param month
	 * @param manufacturersName
	 * @param page
	 * @param rows
	 */
	@RequestMapping("/getMoney.do")
	public void getMoney(HttpServletResponse response, @RequestParam(value = "userid", required = false) String userid,
			@RequestParam(value = "userid_sj", required = false) String userid_sj,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "year", required = false) String year,
			@RequestParam(value = "month", required = false) String month, int page, int rows) {
		CommonResult commonResult = new CommonResult();
		try {
			if (StringUtils.isNotEmpty(userid)) {

				Criteria criteria = new Criteria();
				criteria.addCriterion(new Criterion(" khid = " + userid ));
				if(StringUtils.isNotEmpty(userid_sj)){
					criteria.addCriterion(new Criterion(" cjid= "+userid_sj+" "));
				}
				//criteria.addCriterion(new Criterion(" cssj < now() "));
				if (StringUtils.isNotEmpty(type)) {
					if ("2".equals(type)) {
						criteria.addCriterion(new Criterion("status <> '6' "));
					} else if ("3".equals(type)) {
						criteria.addCriterion(new Criterion("status =  '6' "));
					}
				}
				if (StringUtils.isNotEmpty(year)) {
					criteria.addCriterion(new Criterion("SUBSTRING(cssj,1,4)=", year));
				}
				if (StringUtils.isNotEmpty(month)) {
					criteria.addCriterion(new Criterion("SUBSTRING(cssj,6,2)=", month));
				}
				PageRequest pageRequest = new PageRequest();
				// 设置分页查询参数
				pageRequest.setPageSize(rows);
				pageRequest.setPageNo(page);
				pageRequest.getCriteria().addAll(criteria.getAllCriteria());
				pageRequest.setOrderByClause(" order by cssj desc ");
				Page<DdjJgd> list = jgdService.findPage(pageRequest);
				// 输出json对象
				JSONArray array = new JSONArray();
				JSONObject object = new JSONObject();
				//double useTotal = 0;
				//double totalNumber = 0;
				for (DdjJgd ddjOrder : list.getResult()) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("gdid", ddjOrder.getGdid());
					jsonObject.put("totalPrices", ddjOrder.getTotalprice());
					jsonObject.put("price", ddjOrder.getPrice());
					jsonObject.put("quantity", ddjOrder.getQuantity());
					jsonObject.put("quantitytype", ddjOrder.getQuantitytype());
					jsonObject.put("cssj", DateUtils.getLongDateStr(ddjOrder.getCssj()));

					//useTotal += ddjOrder.getTotalprice();
					int quantitytype = ddjOrder.getQuantitytype();
					if (quantitytype == 1) {
						//totalNumber += Double.parseDouble(ddjOrder.getQuantity());
					}
					array.put(jsonObject);
				}
				object.put("list", array);
				// 总价钱
				//object.put("total", useTotal + "");
				// 总重量
				//object.put("totalnumber", totalNumber + "");
				commonResult.setResult(object);
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("缺少参数");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			commonResult.setCode(XboxUtils.STATUS_ERROR);
		} finally {
			ResManager.getOut(response).print(commonResult.getJsonObject());
			ResManager.close();
		}
	}
	
	
	
	/**
	 * 查询客户或者商家对<br/>
	 * 某个客户或者商家的总未付款或者总未收款|总数量|总金额</br>
	 * @param response
	 * @param userid
	 */
	@RequestMapping("/getTotalPayStat.do")
	public void getTotalPayStat(HttpServletResponse response, @RequestParam(value = "userid_kh", required = false) String userid_kh,@RequestParam(value = "userid_sj", required = false) String userid_sj){
		CommonResult commonResult = new CommonResult();
		try {
			if (StringUtils.isNotEmpty(userid_sj)&&StringUtils.isNotEmpty(userid_kh)) {
				DdjJgd queryJgd = new DdjJgd();
				DdjZdb queryzdb = new DdjZdb();
				queryJgd.setKhid(Long.valueOf(userid_kh));
				queryzdb.setKhbh(Long.valueOf(userid_kh));
				queryJgd.setCjid(Long.valueOf(userid_sj));
				queryzdb.setCjbh(Long.valueOf(userid_sj));
				List<DdjJgd> jgdList=jgdService.getByValues(queryJgd);
				// 输出json对象
				JSONObject object = new JSONObject();
				double useTotal = 0;
				double totalNumber = 0;
				if(null!=jgdList){
					for (DdjJgd ddjOrder : jgdList) {
						useTotal += ddjOrder.getTotalprice();
						int quantitytype = ddjOrder.getQuantitytype();
						if (quantitytype == 1) {
							totalNumber += Double.parseDouble(ddjOrder.getQuantity());
						}
					}
				}
				double payTotal = 0;
				List<DdjZdb> zdblist = zdbService.getByValues(queryzdb);
				if(null!=zdblist){
					for(DdjZdb ddjZdb : zdblist){
						payTotal+=ddjZdb.getPayprice();
					}
				}
				// 总价钱
				object.put("totalPrice", useTotal + "");
				// 总未付款
				object.put("totalNonPayment", (useTotal-payTotal) + "");
				// 总重量
				object.put("totalNumber",totalNumber + "");
				
				commonResult.setResult(object);
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("缺少参数");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			commonResult.setCode(XboxUtils.STATUS_ERROR);
		} finally {
			ResManager.getOut(response).print(commonResult.getJsonObject());
			ResManager.close();
		}
	}
	
	
	/**
	 * 查询总未付款或者总未收款
	 * @param response
	 * @param userid
	 */
	@RequestMapping("/getTotalUnRMoney.do")
	public void getUnRTotalPrice(HttpServletResponse response, @RequestParam(value = "userid", required = false) String userid){
		CommonResult commonResult = new CommonResult();
		try {
			if (StringUtils.isNotEmpty(userid)) {
				DdjJgd queryJgd = new DdjJgd();
				DdjZdb queryzdb = new DdjZdb();
				
				DdjUser user = userService.getById(userid);
				if(null!=user){
					if(user.getUsertype().equals("1")){
						queryJgd.setKhid(Long.valueOf(userid));
						queryzdb.setKhbh(Long.valueOf(userid));
					}
					else if(user.getUsertype().equals("2")){
						queryJgd.setCjid(Long.valueOf(userid));
						queryzdb.setCjbh(Long.valueOf(userid));
					}
				}
				JSONObject object = new JSONObject();
				List<DdjJgd> jgdList=jgdService.getByValues(queryJgd);
				if(null!=jgdList){
					// 输出json对象
					double useTotal = 0;
					for (DdjJgd ddjOrder : jgdList) {
						useTotal += ddjOrder.getTotalprice();
					}
					double payTotal = 0;
					List<DdjZdb> zdblist = zdbService.getByValues(queryzdb);
					for(DdjZdb ddjZdb : zdblist){
						payTotal+=ddjZdb.getPayprice();
					}
					// 总价钱
					object.put("totalprice", (useTotal-payTotal) + "");
					// 总重量
					commonResult.setResult(object);
				}
				else{
					// 总价钱
					object.put("totalprice", "");
					// 总重量
					commonResult.setResult(object);
				}
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("缺少参数");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			commonResult.setCode(XboxUtils.STATUS_ERROR);
		} finally {
			ResManager.getOut(response).print(commonResult.getJsonObject());
			ResManager.close();
		}
	}

	
	
	/**
	 * 客户付款
	 * 
	 * @param response
	 * @param userid
	 * @param cjid
	 * @param payprice
	 */
	@RequestMapping("/payment.do")
	public void payment(HttpServletResponse response, DdjZdb ddjZdb) {

		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();
		try {
			ddjZdb.setStatus((short)0);
			zdbService.save(ddjZdb);
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}
	}

	/**
	 * 查询确认付款列表
	 * 
	 * @param response
	 * @param cjbh
	 * @param rows
	 * @param page
	 */
	@RequestMapping("/queryPayList.do")
	public void queryPayList(HttpServletResponse response, String userid,String status, int rows, int page) {
		CommonResult commonResult = new CommonResult();
		try {
			if (StringUtils.isNotEmpty(userid)) {

				Criteria criteria = new Criteria();
				DdjUser quser = userService.getById(userid);
				
				if(quser.getUsertype().equals("1")){
					criteria.addCriterion(new Criterion(" khbh = " + userid + " "));
				}
				if(quser.getUsertype().equals("2")){
					criteria.addCriterion(new Criterion(" cjbh = " + userid + " "));
				}
				criteria.addCriterion(new Criterion(" status = "+ status+"  "));
				criteria.addCriterion(new Criterion(" ( createtime < now() or createtime  =now() ) "));
				PageRequest pageRequest = new PageRequest();
				// 设置分页查询参数
				pageRequest.setPageSize(rows);
				pageRequest.setPageNo(page);
				pageRequest.getCriteria().addAll(criteria.getAllCriteria());
				pageRequest.setOrderByClause(" order by createtime desc ");
				Page<DdjZdb> list = zdbService.findPage(pageRequest);
				
				// 输出json对象
				JSONArray array = new JSONArray();
				JSONObject object = new JSONObject();
				if(null!=list){
					for (DdjZdb ddjzdb : list.getResult()) {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("zdid", ddjzdb.getId());
						jsonObject.put("khbh", ddjzdb.getKhbh());
						String khname = "";
						DdjUser user = userService.getById("" + ddjzdb.getKhbh());
						khname = user != null ? user.getUsername() : "";
						jsonObject.put("khname", khname);
						jsonObject.put("payprice", ddjzdb.getPayprice());
						jsonObject.put("paytype", ddjzdb.getPaytype());
						jsonObject.put("createtime", DateUtils.getLongDateStr(ddjzdb.getCreatetime()));
	
						array.put(jsonObject);
					}
				}
				object.put("list", array);
				commonResult.setResult(object);
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("缺少参数");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			commonResult.setCode(XboxUtils.STATUS_ERROR);
		} finally {
			ResManager.getOut(response).print(commonResult.getJsonObject());
			ResManager.close();
		}
	}

	/**
	 * 
	 * 确认付款
	 * 
	 * @param response
	 * @param zdid
	 */
	@RequestMapping("/qrPayment.do")
	public void qrPayment(HttpServletResponse response, String zdid) {
		CommonResult commonResult = new CommonResult();
		JSONObject result = commonResult.getJsonObject();
		try {
			if (StringUtils.isNotEmpty(zdid)) {
				DdjZdb zdb = zdbService.getById(zdid);
				zdb.setStatus((short)1);
				zdbService.updateValuesById(zdb);
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("缺少参数");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print(result);
			ResManager.close();
		}
	}

	/**
	 * 检测版本更新/更改服务端版本号
	 * 
	 * @param response
	 * @param official
	 */
	@RequestMapping("/official/isUpdate.do")
	public void isUpdate(HttpServletResponse response, Official official) {

		CommonResult commonResult = new CommonResult();
		JSONObject jsonObject = null;
		// 获取输入
		try {
			if (StringUtils.isNotEmpty(official.getVersion()) && StringUtils.isNotEmpty(official.getType())) {
				String[] ver = official.getVersion().split(",");
				if (ver[0].equals("admin")) {
					official.setId(HafProperties.version_id);
					officialService.updateValuesById(official);
				} else {
					Map<String, String> map = new HashMap<String, String>();
					Official off = officialService.getById(HafProperties.version_id);
					if ("android".equals(official.getType())) {
						if (!official.getVersion().equals(off.getVersion())) {
							map.put("url", off.getPhonos());
						} else {
							commonResult.setCode(XboxUtils.STATUS_ERROR);
							commonResult.setMessage("当前为最新版本");
						}
					} else if ("ios".equals(official.getType())) {
						if (!official.getVersion().equals(off.getVersionIos())) {
							map.put("url", off.getContent().toString());
						} else {
							commonResult.setCode(XboxUtils.STATUS_ERROR);
							commonResult.setMessage("当前为最新版本");
						}
					}
					jsonObject = new JSONObject(commonResult);
					jsonObject.put("result", map);
				}
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
			commonResult.setCode(XboxUtils.STATUS_ERROR);
		} finally {
			ResManager.getOut(response).print(jsonObject);
			ResManager.close();
		}
	}

	// ==================================================web端=============================================//

	@RequestMapping("/ddjUser/index.do")
	public String index() {
		return "/grid/ddjUsersGrid";
	}

	@RequestMapping("/ddjUser/list/getUserList.do")
	public void getUserList(HttpServletResponse response,
			@RequestParam(value = "usertype", required = false) String usertype,
			@RequestParam(value = "telephone", required = false) String telephone,
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "audit", required = false) String audit,
			@RequestParam(value = "companyname", required = false) String companyname, int rows, int page) {

		try {
			// 设置查询条件
			Criteria criteria = new Criteria();
			criteria.addCriterion(new Criterion("usertype in (1,2) "));
			if (StringUtils.isNotEmpty(usertype)) {
				criteria.addCriterion(new Criterion("usertype=" + usertype + " "));
			}
			if (StringUtils.isNotEmpty(audit)) {
				criteria.addCriterion(new Criterion("audit=" + audit + " "));
			}
			if (StringUtils.isNotEmpty(telephone)) {
				criteria.addCriterion(new Criterion("telephone='" + telephone + "' "));
			}
			if (StringUtils.isNotEmpty(companyname)) {
				// criteria.addCriterion(new Criterion("audit="+audit+" "));
			}
			if (StringUtils.isNotEmpty(username)) {
				criteria.addCriterion(new Criterion("username LIKE '%" + username + "%'"));
			}

			PageRequest pageRequest = new PageRequest();
			// 设置分页查询参数
			pageRequest.setPageSize(rows);
			pageRequest.setPageNo(page);
			pageRequest.getCriteria().addAll(criteria.getAllCriteria());
			pageRequest.setOrderByClause("order by usertype,username");
			// 记录总数
			Page<DdjUser> list = userService.findPage(pageRequest);

			// 输出json对象
			JSONObject json = new JSONObject();
			JSONArray array = new JSONArray();

			for (DdjUser ddjUser : list.getResult()) {

				JSONObject jsonObject = new JSONObject(ddjUser);
				Long companyid = ddjUser.getCompanyid();
				if (null != companyid) {
					DdjCompany ddjCompany = companyService.getById("" + companyid.longValue());
					if (null != ddjCompany) {
						jsonObject.put("companyname", ddjCompany.getCompanyname());
						jsonObject.put("intro", ddjCompany.getIntro());
						jsonObject.put("address", ddjCompany.getAddress());
						jsonObject.put("logo", ddjCompany.getLogo());
						jsonObject.put("jyfw", ddjCompany.getJyfw());
						jsonObject.put("sortid", ddjCompany.getSortid());
						jsonObject.put("pfen", ddjCompany.getPfen());
					}
				}
				jsonObject.put("id", ddjUser.getUserid());
				jsonObject.put("detail", "");
				array.put(jsonObject);
			}
			json.put("total", list.getTotalItems());
			json.put("rows", array);
			ResManager.getOut(response).print(json);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.getMessage();
		} finally {
			ResManager.close();
		}

	}

	@RequestMapping("/ddjUser/updateUserAudit.do")
	public void updateUserAudit(HttpServletResponse response, @RequestBody List<Integer> userids) {
		CommonResult commonResult = new CommonResult();
		try {
			if (userids != null && userids.size() > 0) {
				for (int i = 0; i < userids.size(); i++) {
					DdjUser user = userService.getById(userids.get(i).intValue() + "");
					user.setAudit((short) 1);
					userService.updateValuesById(user);
				}
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("缺少参数");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print("{\"success\":true}");
			ResManager.close();
		}
	}

	/**
	 * 设置审核不通过
	 * 
	 * @param response
	 * @param userids
	 */
	@RequestMapping("/ddjUser/auditUserForNo.do")
	public void auditUserForNo(HttpServletResponse response, @RequestBody List<Integer> userids) {
		CommonResult commonResult = new CommonResult();
		try {
			if (userids != null && userids.size() > 0) {
				for (int i = 0; i < userids.size(); i++) {
					DdjUser user = userService.getById(userids.get(i).intValue() + "");
					user.setAudit((short) 0);
					userService.updateValuesById(user);
				}
			} else {
				commonResult.setCode(XboxUtils.STATUS_ERROR);
				commonResult.setMessage("缺少参数");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			commonResult.setCode(XboxUtils.STATUS_ERROR);
			e.printStackTrace();
		} finally {
			ResManager.getOut(response).print("{\"success\":true}");
			ResManager.close();
		}
	}

}
