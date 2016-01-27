package com.jdy.ddj.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 */
public class ResTool {
    private static Logger logger = LoggerFactory.getLogger(ResTool.class);
    private static PrintWriter out;

    /****
     * json格式输出
     * @param response
     * @return
     */
    public static PrintWriter getOut(HttpServletResponse response){
        response.setContentType("application/json; charset=utf-8");
        response.setHeader( "Pragma", "no-cache");
        response.addHeader( "Cache-Control", "must-revalidate");
        response.addHeader( "Cache-Control", "no-cache");
        response.addHeader( "Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
        try {
            out = response.getWriter();
        } catch (IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
        return out;
    }


    /****
     * 文本形式输出
     * @param response
     * @return
     */
    public static PrintWriter getTextOut(HttpServletResponse response){
        response.setContentType("text/html; charset=utf-8");
        response.setHeader( "Pragma", "no-cache");
        response.addHeader( "Cache-Control", "must-revalidate");
        response.addHeader( "Cache-Control", "no-cache");
        response.addHeader( "Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
        try {
            out = response.getWriter();
        } catch (IOException e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }
        return out;
    }

    public static void close(){
        if(out!=null){
            out.flush();
            out.close();
        }
    }

}
