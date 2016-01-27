package com.jdy.ddj.utils;


import com.jdy.ddj.common.utils.HafProperties;
import com.jdy.ddj.filter.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Set;

/**
 * Created by Tianding on 2015-07-16.
 */
public class LoginUtils {
    private static Logger logger = LoggerFactory.getLogger(LoginUtils.class);


    //获取当前登陆的sessionId
    public static Object getLoginSession(HttpServletRequest request){
        Object session=null;
        if(request.getSession().getAttribute(HafProperties.LAST_LOGIN_PERSON)!=null){
            session=request.getSession().getAttribute(HafProperties.LAST_LOGIN_PERSON);
        }
        return session;
    }

    public static String setLoginSession(HttpServletRequest request,Object object,String type){
        request.getSession().setAttribute(HafProperties.LAST_LOGIN_PERSON,object);
        request.getSession().setAttribute(HafProperties.LAST_LOGIN_PERSON_TYPE,type);
        return request.getSession().getId();
    }

   //注销当前登陆的sessionId
   public static void setLogout(HttpServletRequest request){
       //clear session
       HttpSession session = request.getSession(false);
       if(null!=session){
           session.invalidate();
       }
   }

    /**
     * 删除cookie @param response
     * @param name
     */
    public static void deleteCookie(HttpServletResponse response, String name ){
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }



    /**
     * 写一个判断用户是否已经登陆的方法
     * @param userId
     * @return
     */
    public static HttpSession  getSessionId(String userId) {
        Set<HttpSession> keys = SessionListener.loginUser.keySet();
        for (HttpSession key : keys) {
            if (SessionListener.loginUser.get(key).equals(userId)) {
                return key;
            }
        }
        return null;
    }


}
