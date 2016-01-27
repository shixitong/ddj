package com.jdy.ddj.common.utils;

/**
 * User: zhangjunhui
 * Date: 13-12-13
 * Time: 下午1:34
 * Haf-oa平台及子系统 Properties 预加载
 */
public class HafProperties {

    public static String LAST_LOGIN_USERNAME;           //最后一次登录账号
    public static String LAST_LOGIN_EXCEPTION;          //最后一次登录异常
    public static String LAST_LOGIN_USER;               //最后一次登录的用户对象
    public static String LAST_LOGIN_PERSON;             //最后一次登录的用户人员对象
    public static String LAST_LOGIN_TIME;               //最后一次登录的时间


    public static String LAST_REQUEST_URL;              //最后一次有效访问地址

    public static String LAST_LOGIN_COOKIE;              //最后一次登陆的cookie

    public static String PERSON_TYPE1;              //角色类型1-超级管理员
    public static String PERSON_TYPE2;              //角色类型1-系部管理员
    public static String PERSON_TYPE3;              //角色类型1-辅导员
    public static String PERSON_TYPE4;              //角色类型1-班级管理员
    public static String PERSON_TYPE5;              //角色类型1-学生

    public static Integer FLOW_STATUS0;            //流程状态 草稿0
    public static Integer FLOW_STATUS1;              //流程状态 审批中1
    public static Integer FLOW_STATUS2;              //流程状态 完成2
    public static Integer FLOW_STATUS3;              //流程状态 申诉
    public static Integer FLOW_STATUS_1;            //流程状态  提交-1


    public static String xx="xx";            //学习
    public static String xwsz="xwsz";       //行为素质
    public static String hd="hd";            //活动
    public static String fj="fj";            //附加
    public static String LAST_MOBILE_CODE="LAST_MOBILE_CODE";            //验证码
    public static String LAST_LOGIN_PERSON_TYPE="LAST_LOGIN_PERSON_TYPE";  //人员类型
    public  static String version_id="version_ios_android";



    //
    static {
        init();
    }

    public static void init(){

        LAST_LOGIN_USERNAME = "SPRING_SECURITY_LAST_USERNAME";
        LAST_LOGIN_EXCEPTION = "SPRING_SECURITY_LAST_EXCEPTION";

        LAST_LOGIN_USER = "SYS_LAST_LOGIN_USER";
        LAST_LOGIN_PERSON = "SYS_LAST_LOGIN_PERSON";
        LAST_LOGIN_TIME = "SYS_LAST_LOGIN_TIME";
        LAST_REQUEST_URL = "SYS_LAST_REQUEST_URL";
        LAST_LOGIN_COOKIE="075df5c1378a417d9b5c7ac98ac89d0d";

        PERSON_TYPE1="1";
        PERSON_TYPE2="2";
        PERSON_TYPE3="3";
        PERSON_TYPE4="4";
        PERSON_TYPE5="5";
        FLOW_STATUS0=0;
        FLOW_STATUS1=1;
        FLOW_STATUS2=2;
        FLOW_STATUS3=3;
        FLOW_STATUS_1=-1;

    }


}
