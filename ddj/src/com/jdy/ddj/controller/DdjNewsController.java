package com.jdy.ddj.controller;


import com.jdy.ddj.common.dao.Criteria;
import com.jdy.ddj.common.dao.Criterion;
import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import com.jdy.ddj.entity.DdjNews;
import com.jdy.ddj.service.DdjNewsService;
import com.jdy.ddj.utils.CommonResult;
import com.jdy.ddj.utils.ResManager;
import com.jdy.ddj.utils.XboxUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Tianding
 */
@Controller
public class DdjNewsController {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(DdjNewsController.class);
    @Autowired
    private DdjNewsService ddjNewsService;


    /**
     * 11.	获取广告列表/广告滚动条
     * @param response
     */
    @RequestMapping("/news/getAllList.do")
    public void getAll(HttpServletResponse response,HttpServletRequest request,
                       @RequestParam(value = "type",required = false)String type,
                       int page,int rows
                       ){
        CommonResult commonResult=new CommonResult();
        try{
            //设置查询条件
            Criteria criteria = new Criteria();
            if(StringUtils.isNotEmpty(type)){
                criteria.addCriterion(new Criterion("type =",type));
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
                jsonObject.put("title",ddjNews.getTitle());
                jsonObject.put("photo",ddjNews.getPhoto());
                if(ddjNews.getDetail()!=null){
                    jsonObject.put("detail","");
                }
                String path = request.getContextPath();
                String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
                jsonObject.put("url", basePath+"/ddj/ddjNews/detail.do?id="+ddjNews.getId());
                array.put(jsonObject);
            }
            json.put("list", array);
            commonResult.setResult(array);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            commonResult.setCode(XboxUtils.STATUS_ERROR);
        }
        finally {
            ResManager.getOut(response).print(commonResult.getJsonObject());
            ResManager.close();
        }
    }

}

