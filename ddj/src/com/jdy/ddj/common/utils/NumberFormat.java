package com.jdy.ddj.common.utils;

import java.text.DecimalFormat;

/**
 * ISConsole Copyright 2012 huaxincem, Co.ltd .
 * Package: cn.springmvc.common.utils
 * FileName: NumberFormat
 * 功能描述：
 * Version：1.0
 * Author: 创建人 JinShaowei
 * Date: 日期 12-11-20
 * Time: 时间 上午9:23
 * Author: 修改人
 * Date: 日期
 * Time: 时间
 * 具体描述:
 */
public class NumberFormat {

    public static double formatNumer(double num1,double num2){
        double val = (num1-num2)/num2;
        DecimalFormat df = new DecimalFormat("#.##");
        String strVal = df.format(val*100);
        return Double.valueOf(strVal);
    }

    /**
     * 字符前补，返回固定长度字符串：如 000001
     * @param num
     * @param length
     * @param fillStr
     * @return
     */
    public static String fillFront(String num, int length, String fillStr){
        String numStr = num;
        for(int i=numStr.length();i<length;i++){
            numStr = fillStr + numStr;
        }
        return numStr;
    }

    public static String fillZero(String numStr, int length){
        return fillFront(numStr, length, "0");
    }
    public static String fillZero(int num, int length){
        return fillFront(String.valueOf(num), length, "0");
    }

    /**
     * 字符后补 A0000
     * @param num
     * @param length
     * @param fillStr
     * @return
     */
    public static String fillBack(String num, int length, String fillStr){
        String numStr = num + "";
        for(int i=numStr.length();i<length;i++){
            numStr += fillStr;
        }
        return numStr;
    }

    public static String fillZeroBack(String numStr, int length){
        return fillBack(numStr, length, "0");
    }
    public static String fillZeroBack(int num, int length){
        return fillBack(num+"", length, "0");
    }

}
