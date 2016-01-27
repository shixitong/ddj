package com.jdy.ddj.controller.web;


import com.jdy.ddj.common.dao.Criteria;
import com.jdy.ddj.common.dao.Criterion;
import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.entity.DdjNews;
import com.jdy.ddj.service.DdjNewsService;
import com.jdy.ddj.utils.ResManager;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by template
 */
@Controller
@RequestMapping("/ddj/ddjNews")
public class DdjNewsWebController {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(DdjNewsWebController.class);
    @Autowired
    private DdjNewsService ddjNewsService;


    /**
     * @return ddjNewsGrid.jsp 页面
     */
    @RequestMapping("index.do")
    public String index(){
        return "/grid/ddjNewsGrid";
    }

    /**
     * 编辑详情
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("edit.do")
    public String edit(
            @RequestParam(value = "id",required = false)String id,
            Model model
              ){
            try{
                DdjNews ddjNews=ddjNewsService.getById(id);
                model.addAttribute("ddjNews",ddjNews);
                model.addAttribute("detail",new String(ddjNews.getDetail(),"utf-8"));
            }catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        return "/form/ddjNewsForm";
    }

    /**
     * 显示详情
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("detail.do")
    public String detail(
            @RequestParam(value = "id",required = false)String id,
            Model model
    ){
        try{
            DdjNews ddjNews=ddjNewsService.getById(id);
            model.addAttribute("detail",new String(ddjNews.getDetail(),"utf-8"));
        }catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return "/form/ddjNewsDetail";
    }

    /**
     * 获取list集合
     * @param response

     */
    @RequestMapping("/list/getAllList.do")
    public void getAll(HttpServletResponse response,
                       @RequestParam(value = "type",required = false)String type,
                       @RequestParam(value = "title",required = false)String title,
                       int rows,int page){
        try{
            //设置查询条件
            Criteria criteria = new Criteria();
            if(StringUtils.isNotEmpty(type)){
                criteria.addCriterion(new Criterion("type LIKE '%" + type + "%'"));
            }
            if(StringUtils.isNotEmpty(title)){
                criteria.addCriterion(new Criterion("title LIKE '%" + title + "%'"));
            }

            PageRequest pageRequest = new PageRequest();
            //设置分页查询参数
            pageRequest.setPageSize(rows);
            pageRequest.setPageNo(page);
            pageRequest.getCriteria().addAll(criteria.getAllCriteria());

            //记录总数
            Page<DdjNews> list = ddjNewsService.findPage(pageRequest);

            //输出json对象
            JSONObject json = new JSONObject();
            JSONArray array = new JSONArray();

            for(DdjNews ddjNews : list.getResult()){
                JSONObject jsonObject = new JSONObject(ddjNews);
                jsonObject.put("id",ddjNews.getId());
                jsonObject.put("detail","");
                array.put(jsonObject);
            }

            json.put("total",list.getTotalItems());
            json.put("rows",array);
            ResManager.getOut(response).print(json);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            e.getMessage();
        }
        finally {
            ResManager.close();
        }
    }

    /**
     * 的添加或者更新
     * @param response
     * @param ddjNews
     */
    @RequestMapping("saveOrUpdate.do")
    public void edit(HttpServletResponse response,
                     HttpSession session,
                     @RequestBody DdjNews ddjNews)throws Exception{
        try {
            //PersonInfo personInfo = LoginUtils.getLoginPerson(session);
            if(ddjNews != null){

                if(StringUtils.isNotEmpty(ddjNews.getStrDetail())&&ddjNews.getDetail()==null){
                    ddjNews.setStrDetail(URLDecoder.decode(ddjNews.getStrDetail(),"UTF-8"));
                    byte[] detail=ddjNews.getStrDetail().getBytes("UTF8");
                    ddjNews.setDetail(detail);
                }

                ddjNewsService.saveOrUpdateById(ddjNews);
            }
            ResManager.getOut(response).print("{\"id\":\"" + ddjNews.getId() + "\"}");
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
            ddjNewsService.deleteByIds(ids);
            //ResManager.getOut(response).print(GlobalConst.returnFalse("删除成功"));
            ResManager.getOut(response).print("{\"success\":true}");
            ResManager.close();
        }
    }



}


