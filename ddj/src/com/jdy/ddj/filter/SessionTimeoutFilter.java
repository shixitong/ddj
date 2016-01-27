package com.jdy.ddj.filter;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created with IntelliJ IDEA.
 * User: Tianding
 * Date: 13-7-15
 * Time: 下午4:18
 * 处理session超时 主要针对ajax请求的增强
 */
public class SessionTimeoutFilter implements Filter {

    String notStr="login.do,saveImg.do,getCode.do,validCode.do,isUpdate.do," +
            "vfPhone.do,findPassword.do,alterPassword.do,register.do";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //To change body of implemented methods use File | Settings | File Tiandings.
    }


    @Override
    public void doFilter(ServletRequest request_, ServletResponse response_, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest)request_;
        HttpServletResponse response = (HttpServletResponse)response_;
        response.setContentType("text/html; charset=UTF-8");
        String url=request.getServletPath();

     /*   //不拦截登陆请求
        if(notFilter(url)){
            chain.doFilter(request, response);
        }*/

    /*    HttpSession UserSession = request.getSession();
        if(UserSession.getAttribute(HafProperties.LAST_LOGIN_PERSON) == null){
            CommonResult commonResult=new CommonResult();
            commonResult.setCode(XboxUtils.STATUS_ERROR);
            commonResult.setMessage("登录超时，请重新登录！");
            ResManager.getOut(response).print(new JSONObject(commonResult));
            ResManager.close();
            return;
         }*/

        chain.doFilter(request, response);
    }


    @Override
    public void destroy() {
        //To change body of implemented methods use File | Settings | File Tiandings.
    }



    /**
     * 不需拦截
     * @param url
     * @return
     */
    public boolean notFilter(String url){
        boolean has=false;
        String[] strs=notStr.split(",");
        for(String str:strs){
            if(url.indexOf(str)!=-1){
                has=true;
                break;
            }
        }
        return has;
    }




}
