package com.jdy.ddj.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class XboxUtils {
    public static Properties xbox;
    public static final String STATUS_SUCCESS = "001";
    public static final String STATUS_SUCCESS_MESSAGE = "请求成功";
    public static final String STATUS_ERROR = "002";
    public static final String STATUS_ERROR_MESSAGE = "请求失败";

    public static  String PERSON_TYPE1 = "1";//客户
    public static  String PERSON_TYPE2 = "2";//厂家
    public static  String PERSON_TYPE3 = "3";//厂家仓库
    public static  String PERSON_TYPE4 = "4";//客户仓库
    public static  String PERSON_TYPE5 = "5";//司机

    public static Map<String,String> getDetailMsg(){
    	Map<String,String> msg = new HashMap<String,String>();
    	msg.put("1", "已从客户仓库拉回未加工产品");
    	msg.put("2", "收到司机未加工的产品");
    	msg.put("3", "交由司机部分加工完成的产品");
    	msg.put("4", "交由司机全部加工完成的产品");
    	msg.put("5", "将完成的产品送往仓库");
    	msg.put("6", "部分收到加工完成的产品");
    	msg.put("7","全部收到加工完成的产品");
    	return msg;
    }
}
