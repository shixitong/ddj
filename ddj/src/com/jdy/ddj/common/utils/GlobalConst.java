package com.jdy.ddj.common.utils;

import org.json.JSONException;

/**
 * 全局变量
 */
public class GlobalConst {

    /****
     * 菜单树根节点
     */
    public static final String TREE_ROOT_ID = "ROOT";

    public static String returnTrue(String msg) throws JSONException {
        org.json.JSONObject obj = new org.json.JSONObject();
        obj.put("success", true);
        obj.put("msg", msg+"");
        return obj.toString();
    }

    public static String returnFalse(String msg) throws JSONException {
        org.json.JSONObject obj = new org.json.JSONObject();
        obj.put("success", false);
        obj.put("msg", msg+"");
        return obj.toString();
    }

    /***
     * 返回状态码
     * @param status 返回状态，1是（通过），2否（不通过）,3后台出现异常
     * @param msg 附带的消息
     * @return
     * @throws org.json.JSONException
     */
    public static String returnStatus(Integer status,String msg) throws JSONException {
        org.json.JSONObject obj = new org.json.JSONObject();
        obj.put("status", status);
        obj.put("msg", msg+"");
        return obj.toString();
    }



    public static String returnTrue(String msg,Object object) throws JSONException {
        org.json.JSONObject obj = new org.json.JSONObject();
        obj.put("success", true);
        obj.put("data",object);
        obj.put("msg", msg+"");
        return obj.toString();
    }

}
