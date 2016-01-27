package com.jdy.ddj.common.utils;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created with IntelliJ IDEA.
 * User: yueliang
 * Date: 13-10-15
 * Time: 下午3:19
 * To change this Tianding use File | Settings | File Tiandings.
 */
public class AppUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T>  T getBean(Class<T> type){
       return applicationContext.getBean(type);
    }



    public static  String htmlspecialchars(String str) {
        str = str.replaceAll("&", "&amp;");
        str = str.replaceAll("<", "&lt;");
        str = str.replaceAll(">", "&gt;");
        str = str.replaceAll("\"", "&quot;");
        return str;
    }


    /**
     * 获取得分double
     * @param str
     * @return
     */
    public static Double getScore(String str){
        Double score=0.0;
        if(str.equals("合格")){
            score=60.0;
        }else if(str.equals("良")){
            score=80.0;
        }else if(str.equals("优")){
            score=90.0;
        }else if(str.equals("差")){
            score=40.0;
        }else if(str.equals("中")){
            score=70.0;
        }else if(hasCol(str)){
               score=Double.parseDouble(str.substring(0,str.indexOf("/")));
        }else{
            score=Double.parseDouble(str);
        }

        return score;
    }

    /**
     * 获取得分integer
     * @param str
     * @return
     */
    public static Integer getScoreInt(String str){
        Double score=0.0;
        if(str.equals("合格")){
            score=60.0;
        }else if(str.equals("良")){
            score=80.0;
        }else if(str.equals("优")){
            score=90.0;
        }else if(str.equals("中")){
            score=70.0;
        }else if(str.equals("差")){
            score=40.0;
        }else if(hasCol(str)){
            score=Double.parseDouble(str.substring(0,str.indexOf("/")));
        }else{
            score=Double.parseDouble(str);
        }

        return score.intValue();
    }

    /***
     * 获取整数，去掉excel导入小数点问题
     * @param str
     * @return
     */
    public static Integer getIntStr(String str){
        Double doubleStr=0.0;
        if(str!=null&&str!=""){
            doubleStr=Double.parseDouble(str);
        }
        return doubleStr.intValue();
    }

    /**
     * 判断字符串中是否含有 “/”
     * @param str
     * @return
     */
    public static boolean hasCol(String str){
        Boolean has=false;
        if(str.indexOf("/")>0){
              has=true;
        }
        return has;
    }

    /**国家奖学金1、国家励志奖学金2、院长奖学金3、市政府奖学金4、学校奖学金5
     * 获取奖学金标题
     * @param type
     * @return
     */
    public static String getAwardTitle(String type){
        if("1".equals(type)){
            return "国家奖学金申请单";
        }else if("2".equals(type)){
            return "国家励志奖学金申请单";
        }else if("3".equals(type)){
            return "院长奖学金申请单";
        }else if("4".equals(type)){
            return "市政府奖学金申请单";
        }else if("5".equals(type)){
            return "学校奖学金申请单";
        }else if("6".equals(type)){
            return "学生行为素质评价";
        }
        return "";
    }








}
