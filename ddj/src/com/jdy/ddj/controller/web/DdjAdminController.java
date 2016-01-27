package com.jdy.ddj.controller.web;


import com.jdy.ddj.common.dao.Criteria;
import com.jdy.ddj.common.dao.Criterion;
import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.common.utils.DateUtils;
import com.jdy.ddj.entity.DdjAdmin;
import com.jdy.ddj.service.DdjAdminService;
import com.jdy.ddj.utils.ResManager;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by template
 */
@Controller
@RequestMapping("/ddj/ddjAdmin")
public class DdjAdminController {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(DdjAdminController.class);
    @Autowired
    private DdjAdminService ddjAdminService;


    /**
     * @return ddjAdminGrid.jsp 页面
     */
    @RequestMapping("index.do")
    public String index(){
        return "/grid/ddjAdminGrid";
    }


    /**
     * 管理员登录
     * @param session
     * @param admin
     * @return
     */
    @RequestMapping("login.do")
    public String login(HttpSession session,DdjAdmin admin){
        if(StringUtils.isNotBlank(admin.getAname())&&StringUtils.isNotBlank(admin.getApassword())){
            List<DdjAdmin> loginList=ddjAdminService.getByValues(admin);
            if(loginList!=null&&loginList.size()>0){
                DdjAdmin loginAdmin=loginList.get(0);
                session.setAttribute("loginAdmin",loginAdmin);
                return "admin/manage";
            }else{
                return "login";
            }
        }
        return "login";
    }




    /**
     * 获取list集合
     * @param response
     */
    @RequestMapping("/list/getAllList.do")
    public void getAll(HttpServletResponse response,
                       @RequestParam(value = "aname",required = false)String aname,
                       int rows,int page){
        try{
            //设置查询条件
            Criteria criteria = new Criteria();
            if(StringUtils.isNotEmpty(aname)){
                criteria.addCriterion(new Criterion("aname LIKE '%" + aname + "%'"));
            }

            PageRequest pageRequest = new PageRequest();
            //设置分页查询参数
            pageRequest.setPageSize(rows);
            pageRequest.setPageNo(page);
            pageRequest.getCriteria().addAll(criteria.getAllCriteria());
            pageRequest.setOrderByClause(" order by createdate desc ");
            //记录总数
            Page<DdjAdmin> list = ddjAdminService.findPage(pageRequest);

            //输出json对象
            JSONObject json = new JSONObject();
            JSONArray array = new JSONArray();

            for(DdjAdmin ddjAdmin : list.getResult()){
                JSONObject jsonObject = new JSONObject(ddjAdmin);
                jsonObject.put("id",ddjAdmin.getId());
                jsonObject.put("createdate", DateUtils.getLongDateStr(ddjAdmin.getCreatedate()));
                array.put(jsonObject);
            }

            json.put("total",list.getTotalItems());
            json.put("rows",array);
            ResManager.getOut(response).print(json);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        finally {
            ResManager.close();
        }
    }

    /**
     * 的添加或者更新
     * @param response
     * @param ddjAdmin
     */
    @RequestMapping("saveOrUpdate.do")
    public void edit(HttpServletResponse response,
                     HttpSession session,
                     @RequestBody DdjAdmin ddjAdmin)throws Exception{
        try {
          //  PersonInfo personInfo = LoginUtils.getLoginPerson(session);
            if(ddjAdmin != null){
               // ddjAdmin.setUpdateUser(personInfo.getSysUsername());
                ddjAdminService.saveOrUpdateById(ddjAdmin);
            }
            ResManager.getOut(response).print("{\"id\":\"" + ddjAdmin.getId() + "\"}");
        }catch (Exception e){
            ResManager.getOut(response).print("{\"success\":false,\"message\":\"保存失败\"}");
            e.printStackTrace();
        }finally {
            ResManager.close();
        }
    }

    /**
     * 的删除
     * @param response
     * @param ids
     */
    @RequestMapping("deleteByIds.do")
    public void delete(HttpServletResponse response,
                       @RequestBody List<String> ids )throws Exception{
        if(ids.size()>0){
            ddjAdminService.deleteByIds(ids);
            //ResManager.getOut(response).print(GlobalConst.returnFalse("删除成功"));
            ResManager.getOut(response).print("{\"success\":true}");
            ResManager.close();
        }
    }

}

