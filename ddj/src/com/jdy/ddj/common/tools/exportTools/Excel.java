package com.jdy.ddj.common.tools.exportTools;

//import cn.springmvc.common.utils.Multimedia;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 卢义
 * Date: 14-7-28
 * Time: 下午6:25
 * To change this Tianding use File | Settings | File Tiandings.
 */
public class Excel {

    public static void export(HttpServletResponse response,HttpServletRequest request,List dataList,String[] colTitle,String[] colNames,String fileName) throws Exception{

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        //生成表头
        Row row0 = sheet.createRow(0);

        for(int i=0;i<colTitle.length;i++){
            row0.createCell(i).setCellValue(colTitle[i]);
        }

        for(int i=0;i<dataList.size();i++) {
            Object obj=dataList.get(i);
            Row row = sheet.createRow(i + 1);
            if(obj instanceof Map){
                for(int j=0;j<colNames.length;j++){
                    String name=colNames[j];
                    row.createCell(j).setCellValue( getStr(((Map)obj).get(name)) );
                }
            }else{
                for(int j=0;j<colNames.length;j++){
                    String name=colNames[j];
                    row.createCell(j).setCellValue( getStr(BeanUtils.getProperty(obj, name)) );
                }
            }

        }

        setFileDownloadHeader(response, request, fileName + ".xls", fileName + ".xls");
        workbook.write(response.getOutputStream());
        response.getOutputStream().flush();
    }

    public static String getStr(Object s){
        return s==null ? "" : s.toString();
    }


    public static void setFileDownloadHeader(HttpServletResponse response,
                                             HttpServletRequest request,
                                             String fileName,
                                             String saveName) throws UnsupportedEncodingException {

        String ext = FilenameUtils.getExtension(fileName);
        String mine_type = null;//Multimedia.mime_types.get(ext);

        if (mine_type != null) {
            response.setContentType(mine_type);
        }
        String ua = request.getHeader("user-agent");

        String new_filename = URLEncoder.encode(saveName, "UTF8");
        // 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的

        if (ua != null && ua.indexOf("Firefox") >= 0) {
            response.setHeader("Content-Disposition", "attachment; filename*=\"UTF-8''" + new_filename + "\"");
        } else {
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(saveName, "UTF-8"));
        }
    }


}
