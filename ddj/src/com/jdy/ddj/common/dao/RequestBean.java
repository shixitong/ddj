package com.jdy.ddj.common.dao;


import com.jdy.ddj.common.orm.Page;
import com.jdy.ddj.common.orm.PageRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Alias("RequestBean")
public class RequestBean implements Serializable {

    private int top;

    private PageRequest pageRequest;

    private int offset = 0;
    private int pageSize = 10;

    private String orderByClause;//

    private boolean distinct;

    protected List<Criteria> oredCriteria;

    private long count;

    public boolean getCriteriaValid(){
         for(Criteria criteria:oredCriteria){
             if(criteria.getValid()){
                 return true;
             }
         }
        return false;
    }

    /**
     * 语句中的一些附加条件,用于语句微调,比如,两条sql语句,仅很小部分不同,
     * 如...in...和...not in...
     */
    private Map<String,String> addtionalCriterion=new HashMap<String,String>();

    public Map<String, String> getAddtionalCriterion() {
        return addtionalCriterion;
    }

    public RequestBean setAddtionalCriterion(Map<String, String> addtionalCriterion) {
        this.addtionalCriterion = addtionalCriterion;
        return this;
    }

    public RequestBean addAddtionalCriterion(String key,String value){
        addtionalCriterion.put(key,value);
        return this;
    }



    public static RequestBean request(){
        return new RequestBean();
    }

    public static RequestBean request(PageRequest request){
        return new RequestBean(request);
    }



    public Page getPage(List<?> lists) {
        Page page =null;
        if(pageRequest != null){
            page = new Page(this.pageRequest);
        }else{
            page = new Page(new PageRequest());
        }
        page.setResult(lists);
        page.setTotalItems(this.count);
        page.setOrderBy(pageRequest.getOrderBy());
        page.setOrderDir(pageRequest.getOrderDir());

        return page;
    }

    public int getTop() {
        return top;
    }

    /**
     * sql top语句
     * @param top 取前N条
     * @return
     */
    public RequestBean setTop(int top) {
        if(top<=0){
            throw new RuntimeException("top必须大于等于0");
        }
        this.top = top;
        this.setOffset(0);
        this.setPageSize(top);
        return this;
    }

    /**
     * 分页查询执行成功后,返回总的数据条数
     * @return
     */
    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public int getOffset() {
        return offset;
    }

    public RequestBean setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public int getPageSize() {

        return pageSize;
    }

    public RequestBean setPageSize(int pageSize) {
        if(pageSize >= 500){
            throw new RuntimeException("单次查询最大返回500条数据");
        }
        this.pageSize = pageSize;
        return this;
    }

    public RequestBean() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public RequestBean(PageRequest pageRequest) {
        oredCriteria = new ArrayList<Criteria>();
        if(pageRequest == null){
            throw new RuntimeException("分页数据为空");
        }
        this.pageRequest = pageRequest;
        this.offset = pageRequest.getOffset();
        this.pageSize = pageRequest.getPageSize();
        if(StringUtils.isNotBlank(pageRequest.getOrderByClause())){
            this.orderByClause = pageRequest.getOrderByClause();
        }

        if(StringUtils.isNotBlank(pageRequest.getOrderBy())){

            this.orderByClause = " order by "+pageRequest.getOrderBy() +" "+ pageRequest.getOrderDir();
        }

    }




    public RequestBean setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
        return this;
    }


    public String getOrderByClause() {
        return orderByClause;
    }


    public RequestBean setDistinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    public boolean getDistinct() {
        return distinct;
    }


    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }


    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }


    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }


    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }


    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }


    public RequestBean clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
        return this;
    }
}
