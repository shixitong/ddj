package com.jdy.ddj.common.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ResManager {
    private static Logger logger = LoggerFactory.getLogger(ResManager.class);
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

    public static void printExcelHeader(HttpServletResponse response,String title,String[] colTitles,String filename) throws Exception{
        if("".equals(filename)||filename==null) filename = "exportData";
        /*filename = URLEncoder.encode(filename, "UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename="+filename+".xls");
        response.setContentType("application/ms-excel;charset=UTF-8");*/
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("gb2312"),"ISO-8859-1") + ".xls");
        response.setHeader("Content-Type", "application/vnd.ms-excel;charset=UTF-8");

        out = response.getWriter();

        if(!"".equals(title)&&title!=null){
            out.println("<table border='1'><tr><td colspan='2' bgcolor='#eee'>" + title + "</td></tr></table>");
        }

        out.println("<table border='1'>");
        out.println("<tr>");
        for(int i=0;i<colTitles.length;i++) out.println("<th>" + colTitles[i] + "</th>");
        out.println("</tr>");
    }
    public static void printExcelRows(HttpServletResponse response,List dataList,String[] colNames) throws Exception{
        out = response.getWriter();
        for(Object obj:dataList) {
            out.println("<tr>");
            if(obj instanceof Map){
                for(String name:colNames){
                    out.println("<td style=\"mso-number-format:'\\@'\">" + getStr(((Map)obj).get(name)) + "</td>");
                }
            }else{
                for(String name:colNames){
                    out.println("<td style=\"mso-number-format:'\\@'\">" + getStr(BeanUtils.getProperty(obj, name)) + "</td>");
                }
            }
            out.println("</tr>");
        }
    }
    public static void printExcelEnd(HttpServletResponse response) throws Exception{
        out = response.getWriter();
        out.println("</table>");
    }
    public static void printExcel(HttpServletResponse response,List dataList,String title,String[] colTitles,String[] colNames,String filename) throws Exception{
        out = response.getWriter();
        printExcelHeader(response,title,colTitles,filename);
        printExcelRows(response,dataList,colNames);
        printExcelEnd(response);
    }
    public static String getStr(Object s){
        return s==null ? "" : s.toString();
    }
}
