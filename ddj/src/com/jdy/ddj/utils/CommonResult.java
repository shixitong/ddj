package com.jdy.ddj.utils;

import org.json.JSONObject;

/**
 * ISConsole Copyright 2014 huaxincem, Co.ltd .
 * FileName: CommonResult
 * 功能描述：通用返回结果
 * Version：1.0
 * Author: 创建人 Tianding
 * Date: 日期 14-5-13
 * Time: 时间 上午9:33
 * Author: 修改人
 * Date: 日期
 * Time: 时间
 * 具体描述:
 */
public class CommonResult {

    /***
     * 返回状态码
     * 0 请求处理错误
     * 1 请求正常返回
     */
    private String code ="001";


    /***
     * 结果对象，此对象可以是实体或集合
     */
    private Object result = "";


    /***
     * 描述信息
     */
    private String message = "请求成功！";



    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result =result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;

        if(XboxUtils.STATUS_ERROR.equals(code)){
            this.setMessage(XboxUtils.STATUS_ERROR_MESSAGE);
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**返回json
     *
     * @return
     */
    public JSONObject getJsonObject(){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("code",this.getCode());
            jsonObject.put("message",this.getMessage());
            jsonObject.put("result",this.getResult());
            return  jsonObject;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
