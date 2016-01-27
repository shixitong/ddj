package com.jdy.ddj.filter;

/**
 * Created by Tianding on 15-6-21.
 */


import com.jdy.ddj.common.utils.HafProperties;
import com.jdy.ddj.utils.LoginUtils;

import javax.servlet.http.*;
import java.util.HashMap;
import java.util.Map;


/**
 * 监听session  防止用户重复登录
 */
public class SessionListener implements HttpSessionListener,HttpSessionAttributeListener {

    // 保存当前登录的所有用户
    public static Map<HttpSession,String> loginUser=new HashMap<HttpSession, String>();


    /**
     * session创建时调用这个方法
     * @param arg0
     */
    public void sessionCreated(HttpSessionEvent arg0) {

    }

    /**
     * Session失效或者过期的时候调用的这个方法,
     * @param se
     */
    public void sessionDestroyed(HttpSessionEvent se) {
        // 如果session超时, 则从map中移除这个用户
        try {
            loginUser.remove(se.getSession());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行setAttribute的时候, 当这个属性本来不存在于Session中时, 调用这个方法.
     * @param se
     */
    public void attributeAdded(HttpSessionBindingEvent se) {
        // 如果添加的属性是用户名, 则加入map中
        if (se.getName().equals(HafProperties.LAST_LOGIN_PERSON)) {
            HttpSession httpSession= LoginUtils.getSessionId(se.getValue().toString());
            if(null!=httpSession){
                loginUser.remove(httpSession);
            }
            loginUser.put(se.getSession(),se.getValue().toString());
        }
    }

    /**
     * 当执行removeAttribute时调用的方法
     * @param se
     */
    public void attributeRemoved(HttpSessionBindingEvent se) {
        // 如果移除的属性是用户名, 则从map中移除
        if (se.getName().equals(HafProperties.LAST_LOGIN_PERSON)) {
            try {
                loginUser.remove(se.getSession());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 当执行setAttribute时 ,如果这个属性已经存在, 覆盖属性的时候, 调用这个方法
     * @param se
     */
    public void attributeReplaced(HttpSessionBindingEvent se) {
        // 如果改变的属性是用户名, 则跟着改变map
        if (se.getName().equals(HafProperties.LAST_LOGIN_PERSON)) {
            loginUser.put(se.getSession(),se.getValue().toString());
        }
    }

}
