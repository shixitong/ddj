/*
package com.jdy.jkbg.common.utils;


import cn.springmvc.entity.Person;
import cn.springmvc.filter.SessionListener;
import cn.springmvc.service.PersonService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Set;

*/
/**
 * Created by Tianding on 14-3-5.
 *//*

public class LoginUtils {
    private static Logger logger = LoggerFactory.getLogger(LoginUtils.class);

    private static PersonService personService;

    public  PersonService getPersonService() {
        return personService;
    }

    public  void setPersonService(PersonService personService) {
        LoginUtils.personService = personService;
    }


    //获取当前登录用户
    public static Person getLoginPerson(HttpServletRequest request){
        Person person=(Person)request.getSession().getAttribute(HafProperties.LAST_LOGIN_PERSON);
        return person;
    }
    //获取当前登录
    public static Person getLoginPerson(HttpSession session){
        Person person=(Person)session.getAttribute(HafProperties.LAST_LOGIN_PERSON);
        return person;
    }


    //获取当前登录名
    public static String getLoginUsername(HttpServletRequest request){
        Person person=(Person)request.getSession().getAttribute(HafProperties.LAST_LOGIN_PERSON);
        String username="";
        if(person!=null){
            username=person.getUnno();
        }
        return username;
    }

    //从session中获取登录用户信息
    public static String getLoginUsername(HttpSession session){
        if(session==null){
            return null;
        }
        Person person=(Person)session.getAttribute(HafProperties.LAST_LOGIN_PERSON);
        String username="";
        if(person!=null){
            username=person.getUnno();
        }
        return username;
    }


    */
/**
     * （检验获取cookie）
     * @param request
     * @param response
     * @return
     *//*

    public static Boolean hasCookie(HttpServletRequest request,HttpServletResponse response){
        Boolean hasCookie=false;

        Cookie cookie[]=request.getCookies();
        for(int i=0;cookie!=null&&i<cookie.length;i++){
            if(cookie[i].getName().equals(HafProperties.LAST_LOGIN_COOKIE)){
                String cookieValue=cookie[i].getValue();
                if(StringUtils.isNotBlank(cookieValue)){
                    Person p =personService.getById(cookieValue);
                    if(p!=null){
                        request.getSession().setAttribute(HafProperties.LAST_LOGIN_PERSON,p);
                        hasCookie=true;
                    }
                }
            }
        }
        return hasCookie;
    }

    */
/**
     * 记住密码（设置cookie）
     * @param request
     * @param response
     * @param remember
     * @param personId
     *//*

    public static  void isRmember(HttpServletRequest request,HttpServletResponse response,Boolean remember,String personId){
        if(remember){
            Cookie coo=new Cookie(HafProperties.LAST_LOGIN_COOKIE,personId);
            coo.setMaxAge(3600*24*15);
            coo.setPath("/");
            response.addCookie(coo);
        }
    }

    */
/**
     * 删除cookie @param response
     * @param name
     *//*

    public static void deleteCookie(HttpServletResponse response, String name ){
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }



    //注销：Cookie，Cache
    public static void setLogout(HttpServletRequest request, HttpServletResponse response, boolean clearSession){
        //删除cookie
        deleteCookie(response,HafProperties.LAST_LOGIN_COOKIE);
        //clear session
        if(clearSession){
            HttpSession session = request.getSession(false);
            if(null!=session){
                session.invalidate();
                request.getSession();
            }
        }
    }

    */
/**
     * 写一个判断用户是否已经登陆的方法
     * @param userId
     * @return
     *//*

    public static boolean hasLogin(HttpSession session,String userId) {
        if (SessionListener.loginUser.get(session)!=null&&SessionListener.loginUser.get(session).equals(userId)) {
             return true;
        }
        return false;
    }


    */
/**
     * 写一个判断用户是否已经登陆的方法
     * @param userId
     * @return
     *//*

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
*/
