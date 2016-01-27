package com.jdy.ddj.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: JinShaowei
 * Date: 13-6-2
 * Time: 下午6:03
 * 日期获取和转换工具类
 */
public class DateUtils {

    private static SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat dateFormat4 = new SimpleDateFormat("ddHHmmss");

    /*****
     * 获取年月日字符串   2013-06-02
     * @param date
     * @return
     */
    public static String  getDateStr(Date date){
        //如果时间为空，则获取当前时间
        if(null == date){
            return dateFormat2.format(new Date());
        }
        return dateFormat2.format(date);
    }

    public static String  getDateStr(){
        return getDateStr(null);
    }

    /****
     * 获取带是时分秒的日期字符串   2013-06-02 10:10:10
     * @param date
     * @return
     */
    public static String  getLongDateStr(Date date){
        if(null == date){
            return dateFormat1.format(new Date());
        }
        //return dateFormat.getDateTimeInstance().format(date);  //在jdk6下不支持
        return dateFormat1.format(date);
    }

    public static String  getLongDateStr(){
        return getLongDateStr(null);
    }


    /***
     * 自动根据时间格式长短转化成date类型时间
     * 支持   2013-06-02,2013-06-02 10:10:10  两种格式的字符串转换
     * @param source
     * @return
     */
    public static Date getDate(String source) {
        if(StringUtils.isBlank(source)){return new Date();}

        SimpleDateFormat dateFormat = dateFormat2;
        if(source.length() > 10){
            dateFormat = dateFormat1;
        }
        dateFormat.setLenient(false);
        try {
            return dateFormat.parse(source);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("日期格式错误");
        }
    }
    
    


    public static Date getDate(){
        return getDate(null);
    }


    /***
     * 获取8位数字的字符串  20130602
     * @param date
     * @return
     */
    public static String getDateNum(Date date){
        if(null == date){
            return dateFormat3.format(new Date());
        }
        return dateFormat3.format(date);
    }

    public static String getDateNum(){
        return getDateNum(null);
    }

    /***
     * 获取14位数字的字符串  20130602021212
     * @param date
     * @return
     */
    public static String getLongDateNum(Date date){
        if(null == date){
            return dateFormat4.format(new Date());
        }
        return dateFormat4.format(date);
    }

    public static String getLongDateNum(){
        int s=(int)(Math.random()*9);
            if(s==0){
                s=1;
            }
        return s+getLongDateNum(null);
    }






}
