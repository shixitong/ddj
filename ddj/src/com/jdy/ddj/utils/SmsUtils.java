package com.jdy.ddj.utils;

import com.jdy.ddj.common.utils.HafProperties;

import javax.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

/**
 * Created by Administrator on 15-8-31.
 */
public class SmsUtils {




    /**
     * 获取一个四位数字的验证码
     * @param phone
     * @throws UnsupportedEncodingException 
     */
    public   static  String getCode(String phone,HttpServletRequest request) throws UnsupportedEncodingException{
        String code="";
        for(int i = 0; i < 4;i++) {
            Random random=new Random();
            code+=Math.abs(random.nextInt())%10;
        }
        String context=URLEncoder.encode("尊敬的客户您好，您的手机验证码为："+code+",有效期为1分钟，请您尽快输入验证码完成验证，感谢您的支持！","GBK");
        String param="usr=7135&pwd=ddz@7135hz&mobile="+phone+"&sms="+context+"&extdsrcid";
        String retMsg=XboxWebUtils.sendGet("qd/SMSSendYD",param);
        System.out.println(retMsg);
        return code;
    }


    /**
     * 检查验证码
     * @param phone
     * @param code
     * @param request
     * @return
     */
    public static boolean validCode(String phone,String code,HttpServletRequest request){
        String validStr="";
        if(request.getSession().getAttribute(HafProperties.LAST_MOBILE_CODE)!=null){
            validStr=(String)request.getSession().getAttribute(HafProperties.LAST_MOBILE_CODE);
            String str[]=validStr.split("_");
            if(str[0].equals(phone)&&str[1].equals(code)){//成功后移除验证码
                request.getSession().removeAttribute(HafProperties.LAST_MOBILE_CODE);
                return true;
            }
        }
        return false;
    }

}
