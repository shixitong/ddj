package com.jdy.ddj.controller;


import com.jdy.ddj.utils.CommonResult;
import com.jdy.ddj.utils.ResManager;
import com.jdy.ddj.utils.SmsUtils;
import com.jdy.ddj.utils.XboxUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tianding
 * 
 */
@Controller
public class SmsController {
	
	//日志信息
    private org.slf4j.Logger logger = LoggerFactory.getLogger(SmsController.class);

    /**
     * http://202.91.244.252/qd/SMSSendYD?usr=7135&pwd=ddz@7135hz&mobile=***&sms=尊敬的客户您好，您的手机验证码为：8438,有效期为1分钟，请您尽快输入验证码完成验证，感谢您的支持！&extdsrcid=
	 *用这个链接做测试，SMS的内容必须为上述模板才能收到短信
     * 获取验证码
     * @param request
     * @param response
     */
    @RequestMapping("/getCode.do")
    public void saveImg(HttpServletRequest request,HttpServletResponse response,
                        @RequestParam(value = "phone",required = false)String phone
    )throws Exception{
        CommonResult commonResult=new CommonResult();
        Map<String,String> map=new HashMap<String,String>();

        //获取输入
        try{
            if(StringUtils.isNotEmpty(phone)){
               String code=SmsUtils.getCode(phone,request);
               map.put("code",code);
               commonResult.setResult(map);
            }else{
                commonResult.setCode(XboxUtils.STATUS_ERROR);
            }
        }catch (Exception e) {
            e.printStackTrace();
            commonResult.setCode(XboxUtils.STATUS_ERROR);
            logger.error(e.getMessage());
        } finally {
            ResManager.getOut(response).print(commonResult.getJsonObject());
            ResManager.close();
        }
    }

    /**
     * 验证 验证码
     * @param request
     * @param yzm
     * @param phone
     */
    @RequestMapping("/validCode.do")
    protected void getImg(HttpServletRequest request,
                          @RequestParam(value = "yzm",required =true)String yzm,
                          @RequestParam(value = "phone",required = false)String phone,
                          HttpServletResponse response){
                CommonResult commonResult=new CommonResult();
                try{
                    if(StringUtils.isNotEmpty(phone)&&StringUtils.isNotEmpty(yzm)){
                        if(!SmsUtils.validCode(phone, yzm, request)){
                            commonResult.setCode(XboxUtils.STATUS_ERROR);
                            commonResult.setMessage("验证码输入错误！");
                        }else{
                            commonResult.setMessage("验证成功！");
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    commonResult.setCode(XboxUtils.STATUS_ERROR);
                }finally {
                    ResManager.getOut(response).print(new JSONObject(commonResult));
                    ResManager.close();
                }
        }

}

