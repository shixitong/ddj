package com.jdy.ddj.common.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.util.Map;

/**
 * Created by zhoupeng on 14-9-1.
 */
public class ExportUtils {

    /**
     *
     * @param TemplateName 模板名称
     * @param outFileTile 生成下载文件的标题名
     * @param docType 生成的文件类型 excel/word
     * @param dataMap 模板需要的数据
     * @return
     */
    public static void downloadFileFromFtl(HttpServletRequest request,
                                           HttpServletResponse response,
                                           String TemplateName,
                                           String docType,
                                           String outFileTile,
                                           Map<String,Object> dataMap) throws Exception{
        Configuration conf = new Configuration();
        Template Template=null;
        conf.setDefaultEncoding("UTF-8");
        conf.setClassicCompatible(true);//去掉空值--空值处理
        try{
            freemarker.log.Logger
                    .selectLoggerLibrary(freemarker.log.Logger.LIBRARY_NONE);
           //String path= getClassPath(ExportUtils.class.getName()); //通过Class的名字获取当前工程路径
             String path =request.getSession().getServletContext().getRealPath("/") + "WEB-INF\\classes\\ftl\\";//磁盘路径
            // String path="http://jmbk.v050.10000net.cn/ftl/";
          // String path= request.getContextPath() + "/WEB-INF/ftl/";
           // conf.setTemplateLoader(new RemoteTemplateLoader(path));//远程路径

            String path2=request.getServletPath();
            conf.setDirectoryForTemplateLoading(new File(path));//本地路径

            try {
              //  conf.setTemplateLoader(new RemoteTemplateLoader(""));
//            conf.setServletContextForTemplateLoading();
        //   conf.setClassForTemplateLoading(ExportUtils.getClass(), "/xls"); //获取路径

                Template = conf.getTemplate(TemplateName+".ftl"); //文件名
            }catch (FileNotFoundException e){
                e.printStackTrace();
                throw new FileNotFoundException("文件未找到");
            }
            conf.getURLEscapingCharset();
            String filePath =  StorageService.ImportOrExportTemplate.getBasePath();
            File outFile = null;
            if(docType==null){
                outFile = fileCreate(filePath, TemplateName + Identities.uuid2() + ".xls");  //生成文件
            }else if(docType.equals("word")){
                outFile = fileCreate(filePath, TemplateName + Identities.uuid2()  + ".doc");  //生成文件
            }else{
                outFile = fileCreate(filePath, TemplateName + Identities.uuid2()  + ".xls");  //生成文件
            }
            Writer out = null;
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
            Template.process(dataMap, out);
            out.flush();
            out.close();
            if(outFileTile==null){
                outFileTile = TemplateName;
            }
          //  V5Utils.setFileDownloadHeader(request, response, outFile, outFileTile);
            if("word".equals(docType)){
                response.reset();
                response.setContentType("bin");
                response.setHeader("Content-Disposition", "attachment;filename=" + new String(outFileTile.getBytes("UTF-8"), "ISO-8859-1") + ".doc");
                response.setHeader("Content-Type", "application/vnd.ms-word");
                InputStream is= FileUtils.openInputStream(outFile);
                IOUtils.copy(is, response.getOutputStream());
                is.close();
                response.flushBuffer();
            }else{
                response.reset();
                response.setContentType("bin");
                response.setHeader("Content-Disposition", "attachment;filename=" + new String(outFileTile.getBytes("UTF-8"), "ISO-8859-1") + ".xls");
                response.setHeader("Content-Type", "application/vnd.ms-excel");
                InputStream is= FileUtils.openInputStream(outFile);
                IOUtils.copy(is, response.getOutputStream());
                is.close();
                response.flushBuffer();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static File fileCreate(String fileFolder, String fileName){
        File folder = new File(fileFolder);
        File file = new File(fileFolder+fileName);
        //如果文件夹不存在，则创建文件夹
        if(folder.exists()==false){
            folder.mkdirs();//多级目录
            //folder.mkdir();//只创建一级目录
        }

        //如果文件不存在，则创建文件
        if(file.exists()==false){
            try{
                file.createNewFile();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return file;
    }

    public static String getClassPath(String className) {
        try {
            URL classUrl;
            Class clazz = Class.forName(className);
            classUrl = clazz.getResource("/" + className.replace('.', '/')
                    + ".class");
            if (classUrl != null) {
                return classUrl.getFile().toString().replace(
                        className.replace(".", "/"), "").replaceFirst("/", "")
                        .replace(".class", "");
            }
            return "no class";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }






}
