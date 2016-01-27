package com.jdy.ddj.common.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 一组条件形成的list集合
 */
public class Criteria implements Serializable {
    protected List<Criterion> criteria;

    public Criteria() {
        criteria = new ArrayList<Criterion>();
    }

    public boolean getValid() {
        return criteria.size() > 0;
    }

    public List<Criterion> getAllCriteria() {
        return criteria;
    }

    public List<Criterion> getCriteria() {

        return criteria;
    }

    public Criteria addCriterion(List<Criterion> c){
        this.criteria.addAll(c);
        return this;
    }

    public Criteria addCriterion(Criterion criterion){
//        if (criterion == null) {
//            throw new RuntimeException("Value for condition cannot be null");
//        }
        if (criterion != null) {
            criteria.add(criterion);
        }

        return this;
    }

    /**
     * 单个条件,如id is null
     * @param condition
     */
    public Criteria addCriterion(String condition) {
//        if (condition == null) {
//            throw new RuntimeException("Value for condition cannot be null");
//        }
        if(condition != null){
            criteria.add(new Criterion(condition));
        }
        return this;
    }

    /**
     * 多值条件 id = xxx in (.,......)
     * @param condition
     * @param value
     */
    public Criteria addCriterion(String condition, Object value) {
//        if (value == null) {
//            throw new RuntimeException("Value for cannot be null");
//        }
        if (value != null) {
            criteria.add(new Criterion(condition, value));
        }

        return this;
    }

    /**
     * between in
     * @param condition
     * @param value1
     * @param value2
     */
    public Criteria addCriterion(String condition, Object value1, Object value2) {
//        if (value1 == null || value2 == null) {
//            throw new RuntimeException("Between values for  cannot be null");
//        }
        if (value1 != null && value2 != null) {
            criteria.add(new Criterion(condition, value1, value2));
        }

        return this;
    }
}
